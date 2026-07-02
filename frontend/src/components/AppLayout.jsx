import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const applicantNav = [
  { to: '/app/dashboard',     icon: '⊞',  label: 'Dashboard' },
  { section: 'Learner License' },
  { to: '/app/apply-ll',      icon: '📋', label: 'Apply for LL' },
  { to: '/app/ll-status',     icon: '🔍', label: 'Check LL Status' },
  { section: 'Driving License' },
  { to: '/app/apply-dl',      icon: '🚗', label: 'Apply for DL' },
  { to: '/app/schedule-test', icon: '📅', label: 'Schedule Test' },
  { to: '/app/dl-status',     icon: '🔎', label: 'Check DL Status' },
  { to: '/app/license-details', icon: '🪪', label: 'View License Details' },
];

const rtoNav = [
  { to: '/app/rto-dashboard',              icon: '⊞',  label: 'Dashboard' },
  { section: 'Learner License' },
  { to: '/app/rto/ll-applications',        icon: '📋', label: 'LL Applications' },
  { section: 'Driving License' },
  { to: '/app/rto/dl-applications',        icon: '🚗', label: 'DL Applications' },
  { to: '/app/rto/scheduled-tests',        icon: '📅', label: 'Scheduled Tests' },
  { section: 'All Applications' },
  { to: '/app/rto/applications',           icon: '📂', label: 'All Applications' },
  { to: '/app/rto/search',                 icon: '🔎', label: 'Search' },
  { to: '/app/rto/applicants',             icon: 'A', label: 'Applicants' },
  { to: '/app/rto/generate-license',       icon: '#', label: 'Generate License' },
  { to: '/app/rto/reports',                icon: '%', label: 'Reports' },
];

export default function AppLayout({ children, title, subtitle }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const isRTO = user?.role === 'rto';
  const nav = isRTO ? rtoNav : applicantNav;

  const handleLogout = () => { logout(); navigate('/'); };

  const emailName = user?.email?.split('@')[0] || 'User';
  const initials = emailName.slice(0, 2).toUpperCase();

  return (
    <div className="app-shell">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-brand">
          <div className="sidebar-logo">Vehicle<span>License</span></div>
          <div className="sidebar-role-badge">
            {isRTO ? '🏛️' : '👤'} {isRTO ? 'RTO Officer' : 'Applicant'}
          </div>
        </div>

        <nav className="sidebar-nav">
          {nav.map((item, i) =>
            item.section ? (
              <div key={i} className="nav-section-label">{item.section}</div>
            ) : (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}
              >
                <span className="nav-icon">{item.icon}</span>
                {item.label}
              </NavLink>
            )
          )}
        </nav>

        <div className="sidebar-footer">
          <div className="user-card">
            <div className="user-avatar">{initials}</div>
            <div className="user-details">
              <div className="user-name">{emailName}</div>
              <div className="user-role-tag">{isRTO ? 'RTO Officer' : 'Applicant'}</div>
            </div>
          </div>
          <button className="btn-logout" onClick={handleLogout}>
            ↩ Sign Out
          </button>
        </div>
      </aside>

      {/* Main */}
      <div className="main-content">
        <div className="topbar">
          <div className="topbar-left">
            <div className="topbar-title">{title}</div>
            {subtitle && <div className="topbar-sub">{subtitle}</div>}
          </div>
          <div className="topbar-right">
            <div className="topbar-badge">{isRTO ? 'RTO Officer' : 'Applicant'}</div>
          </div>
        </div>
        <div className="page-content">{children}</div>
      </div>
    </div>
  );
}
