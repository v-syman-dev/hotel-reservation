import { useState, type FormEvent } from 'react';
import styles from '@/app/styles/ui.module.css';
import type { Convenience } from '@/entities/convenience/model/types';

interface ConvenienceFormProps {
  title: string;
  submitLabel: string;
  isSubmitting: boolean;
  initialValue?: Convenience | null;
  onSubmit: (payload: Convenience) => Promise<void>;
  onCancel?: () => void;
}

export function ConvenienceForm({
  title,
  submitLabel,
  isSubmitting,
  initialValue,
  onSubmit,
  onCancel,
}: ConvenienceFormProps) {
  const [name, setName] = useState(initialValue?.name ?? '');
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!name.trim()) {
      setError('Convenience name cannot be empty.');
      return;
    }

    await onSubmit({
      id: initialValue?.id,
      name: name.trim(),
    });
  };

  return (
    <section className={styles.panel}>
      <div className={styles.sectionHeading}>
        <div>
          <p className={styles.eyebrow}>Many-to-many catalog</p>
          <h2>{title}</h2>
        </div>
      </div>

      <form className={styles.stackForm} onSubmit={handleSubmit}>
        <label className={styles.field}>
          <span>Name</span>
          <input value={name} onChange={(event) => setName(event.target.value)} placeholder="Wi-Fi" />
        </label>

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
