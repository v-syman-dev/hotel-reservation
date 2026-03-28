import { useDeferredValue, useEffect, useState } from 'react';
import { BedDouble, Filter, MapPin, Plus, Search, Trash2 } from 'lucide-react';
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
          <p className={styles.eyebrow}>SPA dashboard</p>
          <h1>Hotel inventory and relation management</h1>
          <p className={styles.heroCopy}>
            This screen covers hotel CRUD, API filtering, local search and a live preview of OneToMany and
            ManyToMany relations before you even open a detail page.
          </p>
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

      <section className={styles.statsGrid}>
        <article className={styles.statCard}>
          <span>Hotels on page</span>
          <strong>{visibleHotels.length}</strong>
        </article>
        <article className={styles.statCard}>
          <span>Total hotels</span>
          <strong>{totalElements}</strong>
        </article>
        <article className={styles.statCard}>
          <span>Known conveniences</span>
          <strong>{conveniences.length}</strong>
        </article>
      </section>

      <div className={styles.pageColumns}>
        <div className={styles.pageMain}>
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

          {error ? <div className={[styles.alert, styles.alertError].join(' ')}>{error}</div> : null}
          {mutationError ? <div className={[styles.alert, styles.alertError].join(' ')}>{mutationError}</div> : null}

          <section className={styles.panel}>
            <div className={styles.sectionHeading}>
              <div>
                <p className={styles.eyebrow}>Hotel list</p>
                <h2>Catalog view</h2>
              </div>
              <div className={styles.metaInline}>
                <Filter size={16} />
                <span>
                  Page {page + 1} of {Math.max(totalPages, 1)}
                </span>
              </div>
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
                      <p className={styles.eyebrow}>Hotel #{hotel.id}</p>
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

                  <div className={styles.chipsRow}>
                    {hotel.conveniences.length > 0 ? (
                      hotel.conveniences.map((item) => (
                        <span key={item.id ?? item.name} className={styles.chip}>
                          {item.name}
                        </span>
                      ))
                    ) : (
                      <span className={[styles.chip, styles.chipMuted].join(' ')}>No conveniences attached</span>
                    )}
                  </div>

                  <div className={styles.relationStrip}>
                    <span>OneToMany: hotel to rooms</span>
                    <span>ManyToMany: hotel &lt;-&gt; conveniences</span>
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
                  <p className={styles.eyebrow}>Workflow</p>
                  <h2>How to use the SPA</h2>
                </div>
              </div>
              <div className={styles.hintList}>
                <p>Create conveniences first if you want to attach a many-to-many set to a hotel immediately.</p>
                <p>Open the hotel details page to manage rooms, bookings, and deeper relation views.</p>
                <p>The country and rating filters call the backend search API directly.</p>
              </div>
            </section>
          )}
        </aside>
      </div>
    </div>
  );
}
