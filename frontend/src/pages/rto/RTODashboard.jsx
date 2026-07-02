import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import AppLayout from '../../components/AppLayout';
import { getAllApplications } from '../../api/api';

export default function RTODashboard() {
  const [apps, setApps]     = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllApplications()
      .then(r => setApps(r.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const total    = apps.length;
  const pending  = apps.filter(a => a.status === 'PENDING').length;
  const approved = apps.filter(a => a.status === 'APPROVED').length;
  const rejected = apps.filter(a => a.status === 'REJECTED').length;

  const stats = [
    { label: 'Total Applications', value: total,    icon: '📋', color: '#1e3a5f' },
    { label: 'Pending Review',     value: pending,  icon: '⏳', color: '#d97706' },
    { label: 'Approved',           value: approved, icon: '✅', color: '#059669' },
    { label: 'Rejected',           value: rejected, icon: '❌', color: '#dc2626' },
  ];

  const quickActions = [
    { to: '/app/rto/ll-applications',  icon: '✅', bg: '#ecfdf5', label: 'LL Applications',     desc: 'Approve or reject learner license applications' },
    { to: '/app/rto/dl-applications',  icon: '🚗', bg: '#eff6ff', label: 'DL Applications',     desc: 'Approve or reject driving license applications' },
    { to: '/app/rto/scheduled-tests',  icon: '📅', bg: '#fff7ed', label: 'Scheduled Tests',     desc: 'Mark pass or fail for driving tests' },
    { to: '/app/rto/applications',     icon: '📂', bg: '#fdf4ff', label: 'All Applications',    desc: 'View and manage all applications' },
    { to: '/app/rto/search',           icon: '🔍', bg: '#fefce8', label: 'Search Applications', desc: 'Search by applicant name or number' },
    { to: '/app/rto/applicants',        icon: 'A', bg: '#ecfeff', label: 'Applicants',           desc: 'Manage applicant records and information' },
    { to: '/app/rto/generate-license',  icon: '#', bg: '#eef2ff', label: 'Generate License',     desc: 'Assign a unique license number' },
    { to: '/app/rto/reports',           icon: '%', bg: '#f0fdf4', label: 'Reports',              desc: 'View application reports and counts' },
  ];

  return (
    <AppLayout title="RTO Dashboard" subtitle="Officer panel — manage all applications">
      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: 48 }}>
          <span className="spinner" style={{ width: 32, height: 32 }} />
        </div>
      ) : (
        <>
          <div className="stats-row">
            {stats.map(s => (
              <div className="stat-card" key={s.label}>
                <div className="stat-icon">{s.icon}</div>
                <div className="stat-value" style={{ color: s.color }}>{s.value}</div>
                <div className="stat-label">{s.label}</div>
              </div>
            ))}
          </div>

          <div className="page-header" style={{ marginTop: 28 }}>
            <h1>Quick Actions</h1>
            <p>Choose an action to manage applications efficiently.</p>
          </div>

          <div className="actions-grid">
            {quickActions.map(a => (
              <Link key={a.to} to={a.to} className="action-card">
                <div className="a-icon" style={{ background: a.bg }}>{a.icon}</div>
                <div>
                  <div className="a-title">{a.label}</div>
                  <div className="a-desc">{a.desc}</div>
                </div>
              </Link>
            ))}
          </div>
        </>
      )}
    </AppLayout>
  );
}
