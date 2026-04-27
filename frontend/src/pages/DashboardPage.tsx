import { useDeferredValue, useEffect, useState } from 'react';
import { BedDouble, MapPin, Plus, Search, Trash2 } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/store/hooks';
import type { Hotel } from '@/entities/hotel/model/types';
import { fetchConveniences } from '@/features/conveniences/model/conveniencesSlice';
import {
  clearHotelMutationState,
  createHotel,
  fetchHotels,
  removeHotel,
  resetServerFilters,
  setCountryFilter,
  setMinRatingFilter,
  setPage,
} from '@/features/hotels/model/hotelsSlice';
import { HotelFilters } from '@/features/hotels/ui/HotelFilters';
import { HotelForm } from '@/features/hotels/ui/HotelForm';
import styles from '@/app/styles/ui.module.css';
import { formatAddress } from '@/shared/lib/format';

export function DashboardPage() {
  const dispatch = useAppDispatch();
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [query, setQuery] = useState('');
  const deferredQuery = useDeferredValue(query);

  const {
    items,
    status,
    error,
    mutationStatus,
    mutationError,
    page,
    size,
    totalPages,
    totalElements,
    filters,
  } = useAppSelector((state) => state.hotels);
  const conveniences = useAppSelector((state) => state.conveniences.items);

  useEffect(() => {
    dispatch(fetchConveniences());
  }, [dispatch]);

  useEffect(() => {
    dispatch(fetchHotels());
  }, [dispatch, filters.country, filters.minRating, page, size]);

  useEffect(() => {
    if (mutationStatus === 'succeeded') {
      dispatch(fetchHotels());
      dispatch(clearHotelMutationState());
    }
  }, [dispatch, mutationStatus]);

  const normalizedQuery = deferredQuery.trim().toLowerCase();
  const visibleHotels = normalizedQuery
    ? items.filter((hotel) =>
        [hotel.name, hotel.address.city, hotel.address.street]
          .join(' ')
          .toLowerCase()
          .includes(normalizedQuery),
      )
    : items;

  const handleCreateHotel = async (payload: Hotel) => {
    await dispatch(createHotel(payload)).unwrap();
    setIsCreateOpen(false);
  };

  const handleDeleteHotel = async (hotelId: number) => {
    if (!window.confirm('Delete this hotel from the system?')) {
      return;
    }

    await dispatch(removeHotel(hotelId)).unwrap();
  };

  return (
    <div className={styles.pageStack}>
      <section className={styles.heroCard}>
        <div>
          <p className={styles.eyebrow}>Hotels Hub</p>
          <h1>Hotel management dashboard</h1>
        </div>

        <div className={styles.heroActions}>
          <button
            type="button"
            className={[styles.button, styles.buttonPrimary].join(' ')}
            onClick={() => setIsCreateOpen((current) => !current)}
          >
            <Plus size={18} />
            <span>{isCreateOpen ? 'Hide form' : 'Create hotel'}</span>
          </button>
        </div>
      </section>

      <div className={styles.pageColumns}>
        <div className={styles.pageMain}>
          {error ? <div className={[styles.alert, styles.alertError].join(' ')}>{error}</div> : null}
          {mutationError ? <div className={[styles.alert, styles.alertError].join(' ')}>{mutationError}</div> : null}

          <section className={styles.panel}>
            <div className={styles.sectionHeading}>
              <div>
                <p className={styles.eyebrow}>Hotel list</p>
                <h2>Catalog view</h2>
              </div>
              <span>Page {page + 1} of {Math.max(totalPages, 1)}</span>
            </div>

            {status === 'loading' ? <div className={styles.emptyState}>Loading hotels from the API...</div> : null}

            {status !== 'loading' && visibleHotels.length === 0 ? (
              <div className={styles.emptyState}>No hotels matched the current server and local filters.</div>
            ) : null}

            <div className={styles.hotelGrid}>
              {visibleHotels.map((hotel) => (
                <article key={hotel.id} className={styles.hotelCard}>
                  <div className={styles.hotelCardTop}>
                    <div>
                      <p className={styles.eyebrow}>Hotel</p>
                      <h3>{hotel.name}</h3>
                    </div>
                    <span className={styles.ratingPill}>{hotel.rating.toFixed(1)}</span>
                  </div>

                  <div className={styles.hotelMeta}>
                    <div className={styles.metaInline}>
                      <MapPin size={16} />
                      <span>{formatAddress(hotel.address)}</span>
                    </div>
                    <div className={styles.metaInline}>
                      <BedDouble size={16} />
                      <span>Rooms: {hotel.rooms.length}</span>
                    </div>
                    <div className={styles.metaInline}>
                      <Search size={16} />
                      <span>Conveniences: {hotel.conveniences.length}</span>
                    </div>
                  </div>

                  <div className={styles.cardActions}>
                    <Link className={[styles.button, styles.buttonPrimary].join(' ')} to={`/hotels/${hotel.id}`}>
                      Open details
                    </Link>
                    <button
                      type="button"
                      className={[styles.button, styles.buttonDanger].join(' ')}
                      onClick={() => hotel.id && handleDeleteHotel(hotel.id)}
                    >
                      <Trash2 size={16} />
                      <span>Delete</span>
                    </button>
                  </div>
                </article>
              ))}
            </div>

            <div className={styles.pagination}>
              <button
                type="button"
                className={[styles.button, styles.buttonGhost].join(' ')}
                disabled={page === 0}
                onClick={() => dispatch(setPage(page - 1))}
              >
                Previous
              </button>
              <button
                type="button"
                className={[styles.button, styles.buttonGhost].join(' ')}
                disabled={totalPages === 0 || page >= totalPages - 1}
                onClick={() => dispatch(setPage(page + 1))}
              >
                Next
              </button>
            </div>
          </section>
        </div>

        <aside className={styles.pageSide}>
          <HotelFilters
            country={filters.country}
            minRating={filters.minRating}
            query={query}
            onCountryChange={(value) => dispatch(setCountryFilter(value))}
            onMinRatingChange={(value) => dispatch(setMinRatingFilter(value))}
            onQueryChange={setQuery}
            onReset={() => {
              dispatch(resetServerFilters());
              setQuery('');
            }}
          />

          {isCreateOpen ? (
            <HotelForm
              key="create-hotel"
              title="Add a hotel"
              conveniences={conveniences}
              submitLabel="Create hotel"
              isSubmitting={mutationStatus === 'loading'}
              onSubmit={handleCreateHotel}
              onCancel={() => setIsCreateOpen(false)}
            />
          ) : (
            <section className={styles.panel}>
              <div className={styles.sectionHeading}>
                <div>
                  <p className={styles.eyebrow}>Summary</p>
                  <h2>Catalog overview</h2>
                </div>
              </div>
              <div className={styles.hintList}>
                <p>Hotels on current page: {visibleHotels.length}</p>
                <p>Total hotels: {totalElements}</p>
                <p>Available conveniences: {conveniences.length}</p>
              </div>
            </section>
          )}
        </aside>
      </div>
    </div>
  );
}
