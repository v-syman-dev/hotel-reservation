import { Building2, Hotel, Layers3, MapPinned } from 'lucide-react';
import { NavLink, Outlet } from 'react-router-dom';
import styles from '@/app/styles/ui.module.css';
import { API_BASE_URL } from '@/shared/api/http';

const navigation = [
  { to: '/', label: 'Hotels Hub', icon: Hotel },
  { to: '/conveniences', label: 'Conveniences', icon: Layers3 },
];

const apiConnectionLabel = API_BASE_URL.startsWith('http')
  ? API_BASE_URL
  : `${API_BASE_URL} (same-origin proxy)`;

export function AppLayout() {
  return (
    <div className={styles.appShell}>
      <aside className={styles.appSidebar}>
        <div className={styles.brandCard}>
          <div className={styles.brandMark}>
            <Building2 size={24} />
          </div>
          <div>
            <p className={styles.eyebrow}>Hotel Reservation SPA</p>
            <h1>Operations Console</h1>
          </div>
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

        <div className={styles.sidebarNote}>
          <MapPinned size={18} />
          <div>
            <p>Connected API base: `{apiConnectionLabel}`.</p>
            <p>One-to-many and many-to-many links are visible directly in the hotel workspace.</p>
          </div>
        </div>
      </aside>

      <main className={styles.appContent}>
        <Outlet />
      </main>
    </div>
  );
}
