import { useEffect, useState } from 'react';
import { Pencil, Plus, Trash2 } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '@/app/store/hooks';
import type { Convenience } from '@/entities/convenience/model/types';
import {
  clearConvenienceMutationState,
  createConvenience,
  fetchConveniences,
  removeConvenience,
  updateConvenience,
} from '@/features/conveniences/model/conveniencesSlice';
import { ConvenienceForm } from '@/features/conveniences/ui/ConvenienceForm';
import styles from '@/app/styles/ui.module.css';

export function ConveniencesPage() {
  const dispatch = useAppDispatch();
  const { items, status, error, mutationStatus, mutationError } = useAppSelector(
    (state) => state.conveniences,
  );
  const [draft, setDraft] = useState<Convenience | null>(null);

  useEffect(() => {
    dispatch(fetchConveniences());
  }, [dispatch]);

  const handleCreate = async (payload: Convenience) => {
    await dispatch(createConvenience(payload)).unwrap();
    dispatch(clearConvenienceMutationState());
    setDraft(null);
  };

  const handleUpdate = async (payload: Convenience) => {
    await dispatch(updateConvenience(payload)).unwrap();
    dispatch(clearConvenienceMutationState());
    setDraft(null);
  };

  const handleDelete = async (convenienceId: number) => {
    if (!window.confirm('Delete this convenience from the catalog?')) {
      return;
    }

    await dispatch(removeConvenience(convenienceId)).unwrap();
    dispatch(clearConvenienceMutationState());
  };

  return (
    <div className={styles.pageStack}>
      <section className={styles.heroCard}>
        <div>
          <p className={styles.eyebrow}>Many-to-many directory</p>
          <h1>Convenience catalog</h1>
          <p className={styles.heroCopy}>
            Manage reusable conveniences here, then attach them to hotels from the detail or creation forms.
          </p>
        </div>

        <div className={styles.heroActions}>
          <button
            type="button"
            className={[styles.button, styles.buttonPrimary].join(' ')}
            onClick={() => setDraft({ name: '' })}
          >
            <Plus size={16} />
            <span>{draft ? 'Form opened' : 'New convenience'}</span>
          </button>
        </div>
      </section>

      {error ? <div className={[styles.alert, styles.alertError].join(' ')}>{error}</div> : null}
      {mutationError ? <div className={[styles.alert, styles.alertError].join(' ')}>{mutationError}</div> : null}

      <div className={styles.pageColumns}>
        <div className={styles.pageMain}>
          <section className={styles.panel}>
            <div className={styles.sectionHeading}>
              <div>
                <p className={styles.eyebrow}>Catalog</p>
                <h2>Available conveniences</h2>
              </div>
            </div>

            {status === 'loading' ? <div className={styles.emptyState}>Loading convenience catalog...</div> : null}

            {status !== 'loading' && items.length === 0 ? (
              <div className={styles.emptyState}>No conveniences yet. Create the first shared amenity.</div>
            ) : null}

            <div className={styles.simpleList}>
              {items.map((item) => (
                <article key={item.id} className={styles.listCard}>
                  <div>
                    <p className={styles.eyebrow}>Convenience #{item.id}</p>
                    <h3>{item.name}</h3>
                  </div>

                  <div className={styles.cardActions}>
                    <button type="button" className={[styles.button, styles.buttonGhost].join(' ')} onClick={() => setDraft(item)}>
                      <Pencil size={16} />
                      <span>Edit</span>
                    </button>
                    <button
                      type="button"
                      className={[styles.button, styles.buttonDanger].join(' ')}
                      onClick={() => item.id && handleDelete(item.id)}
                    >
                      <Trash2 size={16} />
                      <span>Delete</span>
                    </button>
                  </div>
                </article>
              ))}
            </div>
          </section>
        </div>

        <aside className={styles.pageSide}>
          {draft ? (
            <ConvenienceForm
              key={draft.id ?? 'new-convenience'}
              title={draft.id ? 'Edit convenience' : 'Create convenience'}
              submitLabel={draft.id ? 'Save convenience' : 'Create convenience'}
              isSubmitting={mutationStatus === 'loading'}
              initialValue={draft.id ? draft : undefined}
              onSubmit={draft.id ? handleUpdate : handleCreate}
              onCancel={() => setDraft(null)}
            />
          ) : (
            <section className={styles.panel}>
              <div className={styles.sectionHeading}>
                <div>
                  <p className={styles.eyebrow}>Usage</p>
                  <h2>How it connects</h2>
                </div>
              </div>
              <div className={styles.hintList}>
                <p>Conveniences are global shared entities for the many-to-many hotel relation.</p>
                <p>Once created here, they become selectable inside hotel creation and hotel editing forms.</p>
              </div>
            </section>
          )}
        </aside>
      </div>
    </div>
  );
}
