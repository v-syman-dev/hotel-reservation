import { Hotel, Layers3 } from 'lucide-react';
import { Link, NavLink, Outlet } from 'react-router-dom';
import styles from '@/app/styles/ui.module.css';

const navigation = [
  { to: '/', label: 'Hotels Hub', icon: Hotel },
  { to: '/conveniences', label: 'Conveniences', icon: Layers3 },
];

export function AppLayout() {
  return (
    <div className={styles.appShell}>
      <aside className={styles.appSidebar}>
        <div className={styles.brandCard}>
          <div className={styles.brandMark}>
            <img src="/hotel-icon.svg" alt="Hotel logo" className={styles.brandMarkImage} />
          </div>
          <Link to="/" className={styles.brandTextLink}>
            Hotel reservations
          </Link>
        </div>

        <nav className={styles.navList} aria-label="Main navigation">
          {navigation.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                [styles.navLink, isActive ? styles.navLinkActive : ''].filter(Boolean).join(' ')
              }
            >
              <Icon size={18} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

      </aside>

      <main className={styles.appContent}>
        <Outlet />
      </main>
    </div>
  );
}
