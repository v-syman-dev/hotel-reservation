import { useEffect, useState } from 'react';
import { ArrowLeft, BedDouble, CalendarDays, MapPin, Pencil, Plus, Trash2, Workflow } from 'lucide-react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/store/hooks';
import type { Booking } from '@/entities/booking/model/types';
import type { Hotel } from '@/entities/hotel/model/types';
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
  fetchRoomBookings,
  updateBooking,
  updateHotel,
  updateRoom,
} from '@/features/hotel-details/model/hotelDetailsSlice';
import { BookingForm } from '@/features/hotel-details/ui/BookingForm';
import { RoomForm } from '@/features/hotel-details/ui/RoomForm';
import { HotelForm } from '@/features/hotels/ui/HotelForm';
import styles from '@/app/styles/ui.module.css';
import { formatAddress, formatDate, formatMoney } from '@/shared/lib/format';

export function HotelDetailsPage() {
  const { hotelId } = useParams<{ hotelId: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  const parsedHotelId = Number(hotelId);
  const isValidHotelId = Number.isFinite(parsedHotelId) && parsedHotelId > 0;

  const {
    hotel,
    status,
    error,
    mutationStatus,
    mutationError,
    bookingsByRoom,
    bookingStatusByRoom,
    bookingErrorByRoom,
  } = useAppSelector((state) => state.hotelDetails);
  const conveniences = useAppSelector((state) => state.conveniences.items);

  const [isEditingHotel, setIsEditingHotel] = useState(false);
  const [roomDraft, setRoomDraft] = useState<Room | null>(null);
  const [selectedRoomId, setSelectedRoomId] = useState<number | null>(null);
  const [bookingDraft, setBookingDraft] = useState<Booking | null>(null);

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
    const roomIdToLoad =
      selectedRoomId && hotel?.rooms.some((room) => room.id === selectedRoomId)
        ? selectedRoomId
        : (hotel?.rooms[0]?.id ?? null);

    if (!roomIdToLoad) {
      return;
    }

    dispatch(fetchRoomBookings(roomIdToLoad));
  }, [dispatch, hotel, selectedRoomId]);

  useEffect(() => {
    if (mutationStatus === 'succeeded') {
      dispatch(clearHotelDetailsMutationState());
    }
  }, [dispatch, mutationStatus]);

  if (!isValidHotelId) {
    return <div className={styles.emptyState}>Invalid hotel id in the route.</div>;
  }

  const resolvedSelectedRoomId =
    selectedRoomId && hotel?.rooms.some((room) => room.id === selectedRoomId)
      ? selectedRoomId
      : (hotel?.rooms[0]?.id ?? null);
  const activeRoom = hotel?.rooms.find((room) => room.id === resolvedSelectedRoomId) ?? null;
  const roomBookings = activeRoom?.id ? bookingsByRoom[activeRoom.id] ?? [] : [];
  const isBookingsLoading = activeRoom?.id ? bookingStatusByRoom[activeRoom.id] === 'loading' : false;
  const bookingError = activeRoom?.id ? bookingErrorByRoom[activeRoom.id] : null;

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
  };

  const handleUpdateRoom = async (payload: Room) => {
    await dispatch(updateRoom({ hotelId: parsedHotelId, room: payload })).unwrap();
    setRoomDraft(null);
  };

  const handleDeleteRoom = async (roomId: number) => {
    if (!window.confirm('Delete this room?')) {
      return;
    }

    await dispatch(deleteRoom({ hotelId: parsedHotelId, roomId })).unwrap();
    setBookingDraft(null);
  };

  const handleCreateBooking = async (payload: Booking) => {
    if (!activeRoom?.id) {
      return;
    }

    await dispatch(createBooking({ roomId: activeRoom.id, booking: payload })).unwrap();
    setBookingDraft(null);
  };

  const handleUpdateBooking = async (payload: Booking) => {
    if (!activeRoom?.id || !payload.id) {
      return;
    }

    await dispatch(updateBooking({ roomId: activeRoom.id, bookingId: payload.id, booking: payload })).unwrap();
    setBookingDraft(null);
  };

  const handleDeleteBooking = async (bookingId: number) => {
    if (!activeRoom?.id) {
      return;
    }

    if (!window.confirm('Delete this booking?')) {
      return;
    }

    await dispatch(deleteBooking({ roomId: activeRoom.id, bookingId })).unwrap();
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
              <p className={styles.eyebrow}>Hotel detail workspace</p>
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
              <button type="button" className={[styles.button, styles.buttonDanger].join(' ')} onClick={handleDeleteHotel}>
                <Trash2 size={16} />
                <span>Delete hotel</span>
              </button>
            </div>
          </section>

          <section className={styles.statsGrid}>
            <article className={styles.statCard}>
              <span>Rating</span>
              <strong>{hotel.rating.toFixed(1)}</strong>
            </article>
            <article className={styles.statCard}>
              <span>Rooms</span>
              <strong>{hotel.rooms.length}</strong>
            </article>
            <article className={styles.statCard}>
              <span>Conveniences</span>
              <strong>{hotel.conveniences.length}</strong>
            </article>
          </section>

          <div className={styles.pageColumns}>
            <div className={styles.pageMain}>
              <section className={styles.panel}>
                <div className={styles.sectionHeading}>
                  <div>
                    <p className={styles.eyebrow}>Relations</p>
                    <h2>Entity map</h2>
                  </div>
                </div>

                <div className={styles.relationMap}>
                  <div className={styles.relationCard}>
                    <div className={styles.metaInline}>
                      <Workflow size={16} />
                      <strong>OneToMany</strong>
                    </div>
                    <p>Hotel to rooms is rendered below as a live room board.</p>
                  </div>
                  <div className={styles.relationCard}>
                    <div className={styles.metaInline}>
                      <Workflow size={16} />
                      <strong>ManyToMany</strong>
                    </div>
                    <p>Hotel with conveniences is rendered as selectable chips and badges.</p>
                  </div>
                  <div className={styles.relationCard}>
                    <div className={styles.metaInline}>
                      <Workflow size={16} />
                      <strong>Nested CRUD</strong>
                    </div>
                    <p>Room to bookings is handled in the active room panel.</p>
                  </div>
                </div>
              </section>

              <section className={styles.panel}>
                <div className={styles.sectionHeading}>
                  <div>
                    <p className={styles.eyebrow}>Hotel info</p>
                    <h2>Address and conveniences</h2>
                  </div>
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

              <section className={styles.panel}>
                <div className={styles.sectionHeading}>
                  <div>
                    <p className={styles.eyebrow}>Rooms</p>
                    <h2>One-to-many room board</h2>
                  </div>
                  <button
                    type="button"
                    className={[styles.button, styles.buttonPrimary].join(' ')}
                    onClick={() => {
                      setRoomDraft({ number: 0, type: '', pricePerNight: 0 });
                      setBookingDraft(null);
                    }}
                  >
                    <Plus size={16} />
                    <span>Add room</span>
                  </button>
                </div>

                {hotel.rooms.length > 0 ? (
                  <div className={styles.roomGrid}>
                    {hotel.rooms.map((room) => (
                      <article
                        key={room.id}
                        className={[styles.roomCard, room.id === resolvedSelectedRoomId ? styles.roomCardActive : '']
                          .filter(Boolean)
                          .join(' ')}
                      >
                        <button
                          type="button"
                          className={styles.roomSelect}
                          onClick={() => {
                            setSelectedRoomId(room.id ?? null);
                            setBookingDraft(null);
                          }}
                        >
                          <div>
                            <p className={styles.eyebrow}>Room #{room.id}</p>
                            <h3>No. {room.number}</h3>
                          </div>
                          <span className={styles.chip}>{room.type}</span>
                        </button>

                        <p className={styles.roomPrice}>{formatMoney(room.pricePerNight)}</p>

                        <div className={styles.cardActions}>
                          <button type="button" className={[styles.button, styles.buttonGhost].join(' ')} onClick={() => setRoomDraft(room)}>
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
                ) : (
                  <div className={styles.emptyState}>No rooms yet. Add the first room to start booking flow.</div>
                )}
              </section>

              <section className={styles.panel}>
                <div className={styles.sectionHeading}>
                  <div>
                    <p className={styles.eyebrow}>Bookings</p>
                    <h2>Active room booking timeline</h2>
                  </div>
                  {activeRoom ? (
                    <button
                      type="button"
                      className={[styles.button, styles.buttonPrimary].join(' ')}
                      onClick={() =>
                        setBookingDraft({
                          guestName: '',
                          checkInDate: '',
                          checkOutDate: '',
                          room: activeRoom,
                        })
                      }
                    >
                      <Plus size={16} />
                      <span>Add booking</span>
                    </button>
                  ) : null}
                </div>

                {activeRoom ? (
                  <>
                    <div className={styles.detailBox}>
                      <div className={styles.metaInline}>
                        <CalendarDays size={16} />
                        <strong>{`Room ${activeRoom.number} | ${activeRoom.type}`}</strong>
                      </div>
                      <p>{formatMoney(activeRoom.pricePerNight)} per night</p>
                    </div>

                    {bookingError ? <div className={[styles.alert, styles.alertError].join(' ')}>{bookingError}</div> : null}
                    {isBookingsLoading ? <div className={styles.emptyState}>Loading bookings...</div> : null}

                    {!isBookingsLoading && roomBookings.length === 0 ? (
                      <div className={styles.emptyState}>No bookings for the selected room yet.</div>
                    ) : null}

                    <div className={styles.bookingList}>
                      {roomBookings.map((booking) => (
                        <article key={booking.id} className={styles.bookingCard}>
                          <div>
                            <p className={styles.eyebrow}>Booking #{booking.id}</p>
                            <h3>{booking.guestName}</h3>
                          </div>
                          <p>
                            {formatDate(booking.checkInDate)} to {formatDate(booking.checkOutDate)}
                          </p>
                          <div className={styles.cardActions}>
                            <button type="button" className={[styles.button, styles.buttonGhost].join(' ')} onClick={() => setBookingDraft(booking)}>
                              <Pencil size={16} />
                              <span>Edit</span>
                            </button>
                            <button
                              type="button"
                              className={[styles.button, styles.buttonDanger].join(' ')}
                              onClick={() => booking.id && handleDeleteBooking(booking.id)}
                            >
                              <Trash2 size={16} />
                              <span>Delete</span>
                            </button>
                          </div>
                        </article>
                      ))}
                    </div>
                  </>
                ) : (
                  <div className={styles.emptyState}>Select a room to manage its bookings.</div>
                )}
              </section>
            </div>

            <aside className={styles.pageSide}>
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

              {activeRoom && bookingDraft ? (
                <BookingForm
                  key={bookingDraft.id ?? `room-${activeRoom.id ?? 'new'}-booking`}
                  room={activeRoom}
                  title={bookingDraft.id ? 'Edit booking' : 'Create booking'}
                  submitLabel={bookingDraft.id ? 'Save booking' : 'Create booking'}
                  isSubmitting={activeRoom.id ? bookingStatusByRoom[activeRoom.id] === 'loading' : false}
                  initialValue={bookingDraft.id ? bookingDraft : undefined}
                  onSubmit={bookingDraft.id ? handleUpdateBooking : handleCreateBooking}
                  onCancel={() => setBookingDraft(null)}
                />
              ) : null}

              {!isEditingHotel && !roomDraft && !bookingDraft ? (
                <section className={styles.panel}>
                  <div className={styles.sectionHeading}>
                    <div>
                      <p className={styles.eyebrow}>Workspace tips</p>
                      <h2>What you can manage here</h2>
                    </div>
                  </div>
                  <div className={styles.hintList}>
                    <p>Hotel editing updates base fields, address, and convenience assignments.</p>
                    <p>Room cards handle the OneToMany branch from hotel to rooms.</p>
                    <p>Bookings live inside the selected room and show nested CRUD in one SPA flow.</p>
                  </div>
                </section>
              ) : null}
            </aside>
          </div>
        </>
      ) : null}
    </div>
  );
}
