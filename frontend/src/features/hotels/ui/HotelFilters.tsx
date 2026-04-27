import styles from '@/app/styles/ui.module.css';

interface HotelFiltersProps {
  country: string;
  minRating: number;
  query: string;
  onCountryChange: (value: string) => void;
  onMinRatingChange: (value: number) => void;
  onQueryChange: (value: string) => void;
  onReset: () => void;
}

export function HotelFilters({
  country,
  minRating,
  query,
  onCountryChange,
  onMinRatingChange,
  onQueryChange,
  onReset,
}: HotelFiltersProps) {
  return (
    <section className={styles.panel}>
      <div className={styles.sectionHeading}>
        <div>
          <p className={styles.eyebrow}>Filters</p>
          <h2>Search hotels</h2>
        </div>
      </div>

      <div className={styles.filtersGrid}>
        <label className={styles.field}>
          <span>Country</span>
          <input
            value={country}
            onChange={(event) => onCountryChange(event.target.value)}
            placeholder="Belarus, France, USA..."
          />
        </label>

        <label className={styles.field}>
          <span>Min rating</span>
          <input
            type="number"
            min="0"
            max="5"
            step="0.1"
            value={minRating}
            onChange={(event) => onMinRatingChange(Number(event.target.value) || 0)}
          />
        </label>

        <label className={[styles.field, styles.fieldWide].join(' ')}>
          <span>Name or address</span>
          <input
            value={query}
            onChange={(event) => onQueryChange(event.target.value)}
            placeholder="Hotel name, city, street..."
          />
        </label>
      </div>

      <div className={styles.inlineActions}>
        <button type="button" className={[styles.button, styles.buttonGhost].join(' ')} onClick={onReset}>
          Reset filters
        </button>
      </div>
    </section>
  );
}
