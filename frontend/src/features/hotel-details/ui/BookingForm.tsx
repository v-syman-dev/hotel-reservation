import { useState, type ChangeEvent, type FormEvent } from 'react';
import styles from '@/app/styles/ui.module.css';
import type { Booking } from '@/entities/booking/model/types';
import type { Room } from '@/entities/room/model/types';

interface BookingFormProps {
  room: Room;
  title: string;
  submitLabel: string;
  isSubmitting: boolean;
  initialValue?: Booking | null;
  onSubmit: (payload: Booking) => Promise<void>;
  onCancel?: () => void;
}

interface BookingFormState {
  guestName: string;
  checkInDate: string;
  checkOutDate: string;
}

function createInitialState(booking?: Booking | null): BookingFormState {
  return {
    guestName: booking?.guestName ?? '',
    checkInDate: booking?.checkInDate ?? '',
    checkOutDate: booking?.checkOutDate ?? '',
  };
}

export function BookingForm({
  room,
  title,
  submitLabel,
  isSubmitting,
  initialValue,
  onSubmit,
  onCancel,
}: BookingFormProps) {
  const [formState, setFormState] = useState<BookingFormState>(() => createInitialState(initialValue));
  const [error, setError] = useState<string | null>(null);

  const handleChange =
    (field: keyof BookingFormState) => (event: ChangeEvent<HTMLInputElement>) => {
      setFormState((current) => ({
        ...current,
        [field]: event.target.value,
      }));
    };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!formState.guestName.trim()) {
      setError('Guest name is required.');
      return;
    }

    if (!formState.checkInDate || !formState.checkOutDate) {
      setError('Select both dates for the booking.');
      return;
    }

    if (new Date(formState.checkOutDate) <= new Date(formState.checkInDate)) {
      setError('Check-out must be later than check-in.');
      return;
    }

    await onSubmit({
      id: initialValue?.id,
      guestName: formState.guestName.trim(),
      checkInDate: formState.checkInDate,
      checkOutDate: formState.checkOutDate,
      room,
    });
  };

  return (
    <section className={styles.panel}>
      <div className={styles.sectionHeading}>
        <div>
          <p className={styles.eyebrow}>Booking CRUD</p>
          <h2>{title}</h2>
        </div>
      </div>

      <form className={styles.stackForm} onSubmit={handleSubmit}>
        <div className={styles.formGrid}>
          <label className={[styles.field, styles.fieldWide].join(' ')}>
            <span>Guest name</span>
            <input
              value={formState.guestName}
              onChange={handleChange('guestName')}
              placeholder="Ivan Petrov"
            />
          </label>

          <label className={styles.field}>
            <span>Check-in</span>
            <input type="date" value={formState.checkInDate} onChange={handleChange('checkInDate')} />
          </label>

          <label className={styles.field}>
            <span>Check-out</span>
            <input type="date" value={formState.checkOutDate} onChange={handleChange('checkOutDate')} />
          </label>
        </div>

        {error ? <div className={[styles.alert, styles.alertError].join(' ')}>{error}</div> : null}

        <div className={styles.formActions}>
          <button type="submit" className={[styles.button, styles.buttonPrimary].join(' ')} disabled={isSubmitting}>
            {isSubmitting ? 'Saving...' : submitLabel}
          </button>
          {onCancel ? (
            <button type="button" className={[styles.button, styles.buttonGhost].join(' ')} onClick={onCancel}>
              Cancel
            </button>
          ) : null}
        </div>
      </form>
    </section>
  );
}
