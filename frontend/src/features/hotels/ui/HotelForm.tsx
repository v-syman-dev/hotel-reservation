import { useState, type ChangeEvent, type FormEvent } from 'react';
import styles from '@/app/styles/ui.module.css';
import type { Convenience } from '@/entities/convenience/model/types';
import type { Hotel } from '@/entities/hotel/model/types';

interface HotelFormProps {
  title: string;
  conveniences: Convenience[];
  initialValue?: Hotel | null;
  submitLabel: string;
  isSubmitting: boolean;
  onSubmit: (payload: Hotel) => Promise<void>;
  onCancel?: () => void;
}

interface HotelFormState {
  name: string;
  country: string;
  city: string;
  street: string;
  rating: string;
  selectedConvenienceIds: number[];
}

function createInitialState(hotel?: Hotel | null): HotelFormState {
  return {
    name: hotel?.name ?? '',
    country: hotel?.address.country ?? '',
    city: hotel?.address.city ?? '',
    street: hotel?.address.street ?? '',
    rating: hotel?.rating?.toString() ?? '4.0',
    selectedConvenienceIds:
      hotel?.conveniences.map((item) => item.id ?? -1).filter((id) => id > 0) ?? [],
  };
}

export function HotelForm({
  title,
  conveniences,
  initialValue,
  submitLabel,
  isSubmitting,
  onSubmit,
  onCancel,
}: HotelFormProps) {
  const [formState, setFormState] = useState<HotelFormState>(() => createInitialState(initialValue));
  const [error, setError] = useState<string | null>(null);

  const handleChange =
    (field: keyof Omit<HotelFormState, 'selectedConvenienceIds'>) =>
    (event: ChangeEvent<HTMLInputElement>) => {
      setFormState((current) => ({
        ...current,
        [field]: event.target.value,
      }));
    };

  const toggleConvenience = (convenienceId: number) => {
    setFormState((current) => {
      const exists = current.selectedConvenienceIds.includes(convenienceId);

      return {
        ...current,
        selectedConvenienceIds: exists
          ? current.selectedConvenienceIds.filter((item) => item !== convenienceId)
          : [...current.selectedConvenienceIds, convenienceId],
      };
    });
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (
      !formState.name.trim() ||
      !formState.country.trim() ||
      !formState.city.trim() ||
      !formState.street.trim()
    ) {
      setError('Fill in the hotel name and full address before saving.');
      return;
    }

    const rating = Number(formState.rating);

    if (!Number.isFinite(rating) || rating < 1 || rating > 5) {
      setError('Rating must be between 1 and 5.');
      return;
    }

    const selectedConveniences = conveniences.filter((item) =>
      formState.selectedConvenienceIds.includes(item.id ?? -1),
    );

    await onSubmit({
      id: initialValue?.id,
      name: formState.name.trim(),
      rating,
      address: {
        id: initialValue?.address.id,
        country: formState.country.trim(),
        city: formState.city.trim(),
        street: formState.street.trim(),
      },
      rooms: initialValue?.rooms ?? [],
      conveniences: selectedConveniences,
    });
  };

  return (
    <section className={styles.panel}>
      <div className={styles.sectionHeading}>
        <div>
          <p className={styles.eyebrow}>Hotel CRUD</p>
          <h2>{title}</h2>
        </div>
      </div>

      <form className={styles.stackForm} onSubmit={handleSubmit}>
        <div className={styles.formGrid}>
          <label className={styles.field}>
            <span>Hotel name</span>
            <input value={formState.name} onChange={handleChange('name')} placeholder="Riverside Palace" />
          </label>

          <label className={styles.field}>
            <span>Rating</span>
            <input
              type="number"
              step="0.1"
              min="1"
              max="5"
              value={formState.rating}
              onChange={handleChange('rating')}
            />
          </label>

          <label className={styles.field}>
            <span>Country</span>
            <input value={formState.country} onChange={handleChange('country')} placeholder="Belarus" />
          </label>

          <label className={styles.field}>
            <span>City</span>
            <input value={formState.city} onChange={handleChange('city')} placeholder="Minsk" />
          </label>

          <label className={[styles.field, styles.fieldWide].join(' ')}>
            <span>Street</span>
            <input
              value={formState.street}
              onChange={handleChange('street')}
              placeholder="Nezavisimosti Ave 11"
            />
          </label>
        </div>

        <div className={styles.field}>
          <span>Conveniences</span>
          <div className={styles.checkboxGrid}>
            {conveniences.length > 0 ? (
              conveniences.map((item) => (
                <label key={item.id ?? item.name} className={styles.checkboxCard}>
                  <input
                    type="checkbox"
                    checked={formState.selectedConvenienceIds.includes(item.id ?? -1)}
                    onChange={() => toggleConvenience(item.id ?? -1)}
                  />
                  <span>{item.name}</span>
                </label>
              ))
            ) : (
              <p className={styles.hintText}>No conveniences yet. Add them on the catalog page first.</p>
            )}
          </div>
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
