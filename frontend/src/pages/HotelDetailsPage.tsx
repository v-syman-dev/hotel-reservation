import { useEffect, useMemo, useState } from 'react';
import { ArrowLeft, BedDouble, CalendarDays, MapPin, Pencil, Plus, Trash2 } from 'lucide-react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/store/hooks';
import { bookingsApi } from '@/entities/booking/api/bookingsApi';
import type { Booking } from '@/entities/booking/model/types';
import type { Hotel } from '@/entities/hotel/model/types';
import { roomsApi } from '@/entities/room/api/roomsApi';
import type { Room } from '@/entities/room/model/types';
import { fetchConveniences } from '@/features/conveniences/model/conveniencesSlice';
import {
  clearHotelDetails,
  clearHotelDetailsMutationState,
  createBooking,
  createRoom,
  deleteBooking,
  deleteHotel,
  deleteRoom,
  fetchHotelDetails,
  updateBooking,
  updateHotel,
  updateRoom,
} from '@/features/hotel-details/model/hotelDetailsSlice';
import { BookingForm } from '@/features/hotel-details/ui/BookingForm';
import { RoomForm } from '@/features/hotel-details/ui/RoomForm';
import { HotelForm } from '@/features/hotels/ui/HotelForm';
import styles from '@/app/styles/ui.module.css';
import { formatAddress, formatDate, formatMoney } from '@/shared/lib/format';
import type { PagedResponse } from '@/shared/types/api';

const ROOM_PAGE_SIZE = 6;
const BOOKING_PAGE_SIZE = 6;

type ActiveTab = 'rooms' | 'bookings';

function paginateItems<T>(items: T[], page: number, size: number): PagedResponse<T> {
  const totalElements = items.length;
  const totalPages = totalElements === 0 ? 0 : Math.ceil(totalElements / size);
  const safePage = totalPages === 0 ? 0 : Math.min(page, totalPages - 1);
  const start = safePage * size;

  return {
    content: items.slice(start, start + size),
    totalElements,
    totalPages,
    size,
    number: safePage,
    first: safePage === 0,
    last: totalPages === 0 || safePage === totalPages - 1,
  };
}

export function HotelDetailsPage() {
  const { hotelId } = useParams<{ hotelId: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  const parsedHotelId = Number(hotelId);
  const isValidHotelId = Number.isFinite(parsedHotelId) && parsedHotelId > 0;

  const { hotel, status, error, mutationStatus, mutationError } = useAppSelector((state) => state.hotelDetails);
  const conveniences = useAppSelector((state) => state.conveniences.items);

  const [isEditingHotel, setIsEditingHotel] = useState(false);
  const [roomDraft, setRoomDraft] = useState<Room | null>(null);
  const [bookingDraft, setBookingDraft] = useState<Booking | null>(null);
  const [activeTab, setActiveTab] = useState<ActiveTab>('rooms');

  const [roomsPage, setRoomsPage] = useState(0);
  const [roomsData, setRoomsData] = useState<PagedResponse<Room> | null>(null);
  const [roomsLoading, setRoomsLoading] = useState(false);
  const [roomsError, setRoomsError] = useState<string | null>(null);

  const [bookingsPage, setBookingsPage] = useState(0);
  const [bookingsData, setBookingsData] = useState<PagedResponse<Booking> | null>(null);
  const [bookingsLoading, setBookingsLoading] = useState(false);
  const [bookingsError, setBookingsError] = useState<string | null>(null);
  const [selectedBookingRoomId, setSelectedBookingRoomId] = useState<number | null>(null);

  const sortedRooms = useMemo(
    () => [...(hotel?.rooms ?? [])].sort((left, right) => left.number - right.number),
    [hotel?.rooms],
  );

  const loadRoomsPage = async (pageToLoad: number) => {
    if (!isValidHotelId) {
      return;
    }

    setRoomsLoading(true);
    setRoomsError(null);

    try {
      const response = await roomsApi.listByHotelPaged(parsedHotelId, pageToLoad, ROOM_PAGE_SIZE);
      setRoomsData(response);
      setRoomsPage(response.number ?? pageToLoad);
    } catch {
      try {
        const rooms = await roomsApi.listByHotel(parsedHotelId);
        const sorted = [...rooms].sort((left, right) => left.number - right.number);
        const fallback = paginateItems(sorted, pageToLoad, ROOM_PAGE_SIZE);
        setRoomsData(fallback);
        setRoomsPage(fallback.number);
      } catch {
        setRoomsError('Failed to load rooms.');
      }
    } finally {
      setRoomsLoading(false);
    }
  };

  const loadBookingsPage = async (pageToLoad: number) => {
    if (!isValidHotelId) {
      return;
    }

    setBookingsLoading(true);
    setBookingsError(null);

    try {
      const response = await bookingsApi.listByHotelPaged(parsedHotelId, pageToLoad, BOOKING_PAGE_SIZE);
      setBookingsData(response);
      setBookingsPage(response.number ?? pageToLoad);
    } catch {
      try {
        const rooms = sortedRooms.length > 0 ? sortedRooms : await roomsApi.listByHotel(parsedHotelId);
        const bookingsGroups = await Promise.all(
          rooms
            .filter((room) => room.id)
            .map(async (room) => {
              const list = await bookingsApi.listByRoom(room.id as number);
              return list.map((booking) => ({ ...booking, room: booking.room ?? room }));
            }),
        );

        const bookings = bookingsGroups
          .flat()
          .sort((left, right) => {
            const byDate = left.checkInDate.localeCompare(right.checkInDate);

            if (byDate !== 0) {
              return byDate;
            }

            return (left.room?.number ?? 0) - (right.room?.number ?? 0);
          });

        const fallback = paginateItems(bookings, pageToLoad, BOOKING_PAGE_SIZE);
        setBookingsData(fallback);
        setBookingsPage(fallback.number);
      } catch {
        setBookingsError('Failed to load bookings.');
      }
    } finally {
      setBookingsLoading(false);
    }
  };

  useEffect(() => {
    if (!isValidHotelId) {
      return;
    }

    dispatch(fetchHotelDetails(parsedHotelId));
    dispatch(fetchConveniences());

    return () => {
      dispatch(clearHotelDetails());
    };
  }, [dispatch, isValidHotelId, parsedHotelId]);

  useEffect(() => {
    void loadRoomsPage(roomsPage);
  }, [roomsPage, parsedHotelId, isValidHotelId]);

  useEffect(() => {
    void loadBookingsPage(bookingsPage);
  }, [bookingsPage, parsedHotelId, isValidHotelId, sortedRooms]);

  useEffect(() => {
    if (mutationStatus === 'succeeded') {
      dispatch(clearHotelDetailsMutationState());
    }
  }, [dispatch, mutationStatus]);

  useEffect(() => {
    if (!sortedRooms.length) {
      setSelectedBookingRoomId(null);
      return;
    }

    const hasSelected =
      selectedBookingRoomId !== null && sortedRooms.some((room) => room.id === selectedBookingRoomId);

    if (!hasSelected) {
      setSelectedBookingRoomId(sortedRooms[0].id ?? null);
    }
  }, [selectedBookingRoomId, sortedRooms]);

  if (!isValidHotelId) {
    return <div className={styles.emptyState}>Invalid hotel in the route.</div>;
  }

  const selectedBookingRoom = sortedRooms.find((room) => room.id === selectedBookingRoomId) ?? null;

  const handleUpdateHotel = async (payload: Hotel) => {
    await dispatch(updateHotel({ hotelId: parsedHotelId, payload })).unwrap();
    setIsEditingHotel(false);
  };

  const handleDeleteHotel = async () => {
    if (!window.confirm('Delete this hotel and return to the dashboard?')) {
      return;
    }

    await dispatch(deleteHotel(parsedHotelId)).unwrap();
    navigate('/');
  };

  const handleCreateRoom = async (payload: Room) => {
    await dispatch(createRoom({ hotelId: parsedHotelId, room: payload })).unwrap();
    setRoomDraft(null);
    await loadRoomsPage(roomsPage);
  };

  const handleUpdateRoom = async (payload: Room) => {
    await dispatch(updateRoom({ hotelId: parsedHotelId, room: payload })).unwrap();
    setRoomDraft(null);
    await loadRoomsPage(roomsPage);
  };

  const handleDeleteRoom = async (roomId: number) => {
    if (!window.confirm('Delete this room?')) {
      return;
    }

    await dispatch(deleteRoom({ hotelId: parsedHotelId, roomId })).unwrap();
    await loadRoomsPage(roomsPage);
    await loadBookingsPage(bookingsPage);
    setBookingDraft(null);
  };

  const handleCreateBooking = async (payload: Booking) => {
    const roomId = payload.room?.id;

    if (!roomId) {
      return;
    }

    await dispatch(createBooking({ roomId, booking: payload })).unwrap();
    setBookingDraft(null);
    await loadBookingsPage(bookingsPage);
  };

  const handleUpdateBooking = async (payload: Booking) => {
    const roomId = payload.room?.id;

    if (!roomId || !payload.id) {
      return;
    }

    await dispatch(updateBooking({ roomId, bookingId: payload.id, booking: payload })).unwrap();
    setBookingDraft(null);
    await loadBookingsPage(bookingsPage);
  };

  const handleDeleteBooking = async (booking: Booking) => {
    const roomId = booking.room?.id;

    if (!roomId || !booking.id) {
      return;
    }

    if (!window.confirm('Delete this booking?')) {
      return;
    }

    await dispatch(deleteBooking({ roomId, bookingId: booking.id })).unwrap();
    await loadBookingsPage(bookingsPage);
    setBookingDraft(null);
  };

  return (
    <div className={styles.pageStack}>
      <div className={styles.backLinkRow}>
        <Link className={[styles.button, styles.buttonGhost].join(' ')} to="/">
          <ArrowLeft size={16} />
          <span>Back to hotels</span>
        </Link>
      </div>

      {error ? <div className={[styles.alert, styles.alertError].join(' ')}>{error}</div> : null}
      {mutationError ? <div className={[styles.alert, styles.alertError].join(' ')}>{mutationError}</div> : null}
      {status === 'loading' ? <div className={styles.emptyState}>Loading hotel workspace...</div> : null}

      {hotel ? (
        <>
          <section className={styles.heroCard}>
            <div>
              <p className={styles.eyebrow}>Hotel details</p>
              <h1>{hotel.name}</h1>
              <p className={styles.heroCopy}>{formatAddress(hotel.address)}</p>
            </div>

            <div className={styles.heroActions}>
              <button
                type="button"
                className={[styles.button, styles.buttonPrimary].join(' ')}
                onClick={() => setIsEditingHotel((current) => !current)}
              >
                <Pencil size={16} />
                <span>{isEditingHotel ? 'Close editor' : 'Edit hotel'}</span>
              </button>
              <button
                type="button"
                className={[styles.button, styles.buttonDanger].join(' ')}
                onClick={handleDeleteHotel}
              >
                <Trash2 size={16} />
                <span>Delete hotel</span>
              </button>
            </div>
          </section>

          <div className={styles.pageColumns}>
            <div className={styles.pageMain}>
              <section className={styles.panel}>
                <div className={styles.sectionHeading}>
                  <div>
                    <p className={styles.eyebrow}>Catalog</p>
                    <h2>Rooms and bookings</h2>
                  </div>
                </div>
                <div className={styles.inlineActions}>
                  <button
                    type="button"
                    className={[styles.button, activeTab === 'rooms' ? styles.buttonPrimary : styles.buttonGhost].join(
                      ' ',
                    )}
                    onClick={() => setActiveTab('rooms')}
                  >
                    Rooms
                  </button>
                  <button
                    type="button"
                    className={[styles.button, activeTab === 'bookings' ? styles.buttonPrimary : styles.buttonGhost].join(
                      ' ',
                    )}
                    onClick={() => setActiveTab('bookings')}
                  >
                    Bookings
                  </button>
                </div>

                {activeTab === 'rooms' ? (
                  <>
                    <div className={styles.sectionHeading}>
                      <span>
                        Page {roomsPage + 1} of {Math.max(roomsData?.totalPages ?? 0, 1)}
                      </span>
                      <button
                        type="button"
                        className={[styles.button, styles.buttonPrimary].join(' ')}
                        onClick={() => setRoomDraft({ number: 0, type: '', pricePerNight: 0 })}
                      >
                        <Plus size={16} />
                        <span>Add room</span>
                      </button>
                    </div>

                    {roomsError ? <div className={[styles.alert, styles.alertError].join(' ')}>{roomsError}</div> : null}
                    {roomsLoading ? <div className={styles.emptyState}>Loading rooms...</div> : null}

                    {!roomsLoading && (roomsData?.content.length ?? 0) === 0 ? (
                      <div className={styles.emptyState}>No rooms yet.</div>
                    ) : null}

                    <div className={styles.roomGrid}>
                      {roomsData?.content.map((room) => (
                        <article key={room.id ?? room.number} className={styles.roomCard}>
                          <div>
                            <p className={styles.eyebrow}>Room</p>
                            <h3>No. {room.number}</h3>
                          </div>
                          <p>{room.type}</p>
                          <p className={styles.roomPrice}>{formatMoney(room.pricePerNight)}</p>

                          <div className={styles.cardActions}>
                            <button
                              type="button"
                              className={[styles.button, styles.buttonGhost].join(' ')}
                              onClick={() => setRoomDraft(room)}
                            >
                              <Pencil size={16} />
                              <span>Edit</span>
                            </button>
                            <button
                              type="button"
                              className={[styles.button, styles.buttonDanger].join(' ')}
                              onClick={() => room.id && handleDeleteRoom(room.id)}
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
                        disabled={roomsPage === 0}
                        onClick={() => setRoomsPage((current) => Math.max(0, current - 1))}
                      >
                        Previous
                      </button>
                      <button
                        type="button"
                        className={[styles.button, styles.buttonGhost].join(' ')}
                        disabled={roomsData ? roomsPage >= roomsData.totalPages - 1 || roomsData.totalPages === 0 : true}
                        onClick={() => setRoomsPage((current) => current + 1)}
                      >
                        Next
                      </button>
                    </div>
                  </>
                ) : (
                  <>
                    <div className={styles.sectionHeading}>
                      <span>
                        Page {bookingsPage + 1} of {Math.max(bookingsData?.totalPages ?? 0, 1)}
                      </span>
                      <div className={styles.inlineActions}>
                        <label className={styles.field}>
                          <span>Room for new booking</span>
                          <select
                            value={selectedBookingRoomId ?? ''}
                            onChange={(event) =>
                              setSelectedBookingRoomId(event.target.value ? Number(event.target.value) : null)
                            }
                          >
                            {sortedRooms.map((room) => (
                              <option key={room.id ?? room.number} value={room.id ?? ''}>
                                Room {room.number}
                              </option>
                            ))}
                          </select>
                        </label>
                        <button
                          type="button"
                          className={[styles.button, styles.buttonPrimary].join(' ')}
                          disabled={!selectedBookingRoom}
                          onClick={() =>
                            selectedBookingRoom &&
                            setBookingDraft({
                              guestName: '',
                              checkInDate: '',
                              checkOutDate: '',
                              room: selectedBookingRoom,
                            })
                          }
                        >
                          <Plus size={16} />
                          <span>Add booking</span>
                        </button>
                      </div>
                    </div>

                    {bookingsError ? <div className={[styles.alert, styles.alertError].join(' ')}>{bookingsError}</div> : null}
                    {bookingsLoading ? <div className={styles.emptyState}>Loading bookings...</div> : null}

                    {!bookingsLoading && (bookingsData?.content.length ?? 0) === 0 ? (
                      <div className={styles.emptyState}>No bookings yet.</div>
                    ) : null}

                    <div className={styles.bookingList}>
                      {bookingsData?.content.map((booking) => (
                        <article key={booking.id} className={styles.bookingCard}>
                          <div>
                            <p className={styles.eyebrow}>Booking</p>
                            <h3>{booking.guestName}</h3>
                          </div>
                          <p>
                            {formatDate(booking.checkInDate)} to {formatDate(booking.checkOutDate)}
                          </p>
                          <div className={styles.metaInline}>
                            <CalendarDays size={16} />
                            <span>Room {booking.room?.number ?? '-'}</span>
                          </div>
                          <div className={styles.cardActions}>
                            <button
                              type="button"
                              className={[styles.button, styles.buttonGhost].join(' ')}
                              onClick={() => setBookingDraft(booking)}
                            >
                              <Pencil size={16} />
                              <span>Edit</span>
                            </button>
                            <button
                              type="button"
                              className={[styles.button, styles.buttonDanger].join(' ')}
                              onClick={() => handleDeleteBooking(booking)}
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
                        disabled={bookingsPage === 0}
                        onClick={() => setBookingsPage((current) => Math.max(0, current - 1))}
                      >
                        Previous
                      </button>
                      <button
                        type="button"
                        className={[styles.button, styles.buttonGhost].join(' ')}
                        disabled={bookingsData ? bookingsPage >= bookingsData.totalPages - 1 || bookingsData.totalPages === 0 : true}
                        onClick={() => setBookingsPage((current) => current + 1)}
                      >
                        Next
                      </button>
                    </div>
                  </>
                )}
              </section>
            </div>

            <aside className={styles.pageSide}>
              <section className={styles.panel}>
                <div className={styles.sectionHeading}>
                  <h2>Address and conveniences</h2>
                </div>

                <div className={styles.detailsGrid}>
                  <div className={styles.detailBox}>
                    <div className={styles.metaInline}>
                      <MapPin size={16} />
                      <strong>Address</strong>
                    </div>
                    <p>{formatAddress(hotel.address)}</p>
                  </div>
                  <div className={styles.detailBox}>
                    <div className={styles.metaInline}>
                      <BedDouble size={16} />
                      <strong>Conveniences</strong>
                    </div>
                    <div className={styles.chipsRow}>
                      {hotel.conveniences.length > 0 ? (
                        hotel.conveniences.map((item) => (
                          <span key={item.id ?? item.name} className={styles.chip}>
                            {item.name}
                          </span>
                        ))
                      ) : (
                        <span className={[styles.chip, styles.chipMuted].join(' ')}>No conveniences assigned</span>
                      )}
                    </div>
                  </div>
                </div>
              </section>

              {isEditingHotel ? (
                <HotelForm
                  key={`hotel-${hotel.id ?? 'edit'}`}
                  title="Edit hotel"
                  conveniences={conveniences}
                  initialValue={hotel}
                  submitLabel="Save hotel"
                  isSubmitting={mutationStatus === 'loading'}
                  onSubmit={handleUpdateHotel}
                  onCancel={() => setIsEditingHotel(false)}
                />
              ) : null}

              {roomDraft ? (
                <RoomForm
                  key={roomDraft.id ?? 'new-room'}
                  title={roomDraft.id ? 'Edit room' : 'Create room'}
                  submitLabel={roomDraft.id ? 'Save room' : 'Create room'}
                  isSubmitting={mutationStatus === 'loading'}
                  initialValue={roomDraft.id ? roomDraft : undefined}
                  onSubmit={roomDraft.id ? handleUpdateRoom : handleCreateRoom}
                  onCancel={() => setRoomDraft(null)}
                />
              ) : null}

              {bookingDraft && bookingDraft.room ? (
                <BookingForm
                  key={bookingDraft.id ?? `booking-${bookingDraft.room.id ?? 'new'}`}
                  room={bookingDraft.room}
                  title={bookingDraft.id ? 'Edit booking' : 'Create booking'}
                  submitLabel={bookingDraft.id ? 'Save booking' : 'Create booking'}
                  isSubmitting={mutationStatus === 'loading'}
                  initialValue={bookingDraft.id ? bookingDraft : undefined}
                  onSubmit={bookingDraft.id ? handleUpdateBooking : handleCreateBooking}
                  onCancel={() => setBookingDraft(null)}
                />
              ) : null}
            </aside>
          </div>
        </>
      ) : null}
    </div>
  );
}
