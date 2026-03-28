import { useState, type ChangeEvent, type FormEvent } from 'react';
import styles from '@/app/styles/ui.module.css';
import type { Room } from '@/entities/room/model/types';

interface RoomFormProps {
  title: string;
  submitLabel: string;
  isSubmitting: boolean;
  initialValue?: Room | null;
  onSubmit: (payload: Room) => Promise<void>;
  onCancel?: () => void;
}

interface RoomFormState {
  number: string;
  type: string;
  pricePerNight: string;
}

function createInitialState(room?: Room | null): RoomFormState {
  return {
    number: room?.number?.toString() ?? '',
    type: room?.type ?? '',
    pricePerNight: room?.pricePerNight?.toString() ?? '',
  };
}

export function RoomForm({
  title,
  submitLabel,
  isSubmitting,
  initialValue,
  onSubmit,
  onCancel,
}: RoomFormProps) {
  const [formState, setFormState] = useState<RoomFormState>(() => createInitialState(initialValue));
  const [error, setError] = useState<string | null>(null);

  const handleChange =
    (field: keyof RoomFormState) => (event: ChangeEvent<HTMLInputElement>) => {
      setFormState((current) => ({
        ...current,
        [field]: event.target.value,
      }));
    };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const number = Number(formState.number);
    const pricePerNight = Number(formState.pricePerNight);

    if (!Number.isInteger(number) || number <= 0) {
      setError('Room number must be a positive integer.');
      return;
    }

    if (!formState.type.trim()) {
      setError('Room type is required.');
      return;
    }

    if (!Number.isFinite(pricePerNight) || pricePerNight < 10) {
      setError('Price per night must be at least 10.');
      return;
    }

    await onSubmit({
      id: initialValue?.id,
      number,
      type: formState.type.trim(),
      pricePerNight,
    });
  };

  return (
    <section className={styles.panel}>
      <div className={styles.sectionHeading}>
        <div>
          <p className={styles.eyebrow}>Room CRUD</p>
          <h2>{title}</h2>
        </div>
      </div>

      <form className={styles.stackForm} onSubmit={handleSubmit}>
        <div className={styles.formGrid}>
          <label className={styles.field}>
            <span>Room number</span>
            <input value={formState.number} onChange={handleChange('number')} placeholder="305" />
          </label>

          <label className={styles.field}>
            <span>Type</span>
            <input value={formState.type} onChange={handleChange('type')} placeholder="Suite" />
          </label>

          <label className={styles.field}>
            <span>Price per night</span>
            <input
              type="number"
              min="10"
              step="0.01"
              value={formState.pricePerNight}
              onChange={handleChange('pricePerNight')}
              placeholder="180"
            />
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
