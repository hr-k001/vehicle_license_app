import { useEffect, useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { getAllApplications } from '../../api/api';

const badgeClass = { PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected' };

export default function ManageApplications() {
  const [apps, setApps]     = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]   = useState('');
  const [filter, setFilter] = useState('ALL');

  const load = () => {
    setLoading(true);
    getAllApplications()
      .then(r => setApps(r.data))
      .catch(() => setError('Failed to load applications.'))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const shown = filter === 'ALL' ? apps : apps.filter(a => (a.type ?? a.applicationType) === filter);

  return (
    <AppLayout title="Manage Applications" subtitle="US-012 · View all submitted applications">
      <div className="page-header">
        <h1>All Applications</h1>
        <p>View every LL and DL application submitted on the portal.</p>
      </div>

      {error && <div className="alert alert-danger">⚠ {error}</div>}

      <div style={{ display: 'flex', gap: 8, marginBottom: 16, flexWrap: 'wrap' }}>
        {['ALL', 'LL', 'DL'].map(f => (
          <button key={f} onClick={() => setFilter(f)}
            className={filter === f ? 'btn btn-primary' : 'btn btn-outline'}
            style={{ fontSize: '0.82rem', padding: '6px 14px' }}>
            {f === 'ALL' ? '📋 All' : f === 'LL' ? '📄 Learner License' : '🚗 Driving License'}
          </button>
        ))}
        <button className="btn btn-outline" style={{ marginLeft: 'auto', fontSize: '0.82rem', padding: '6px 14px' }} onClick={load}>
          ↻ Refresh
        </button>
      </div>

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: 48 }}>
          <span className="spinner" style={{ width: 32, height: 32 }} />
        </div>
      ) : shown.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📭</div>
          <div className="empty-title">No Applications Found</div>
          <div className="empty-sub">No {filter !== 'ALL' ? filter : ''} applications have been submitted yet.</div>
        </div>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Application No.</th>
                <th>Type</th>
                <th>Applicant Name</th>
                <th>Email</th>
                <th>Status</th>
                <th>Amount Paid</th>
              </tr>
            </thead>
            <tbody>
              {shown.map(a => {
                const name = [a.applicant?.firstName, a.applicant?.middleName, a.applicant?.lastName].filter(Boolean).join(' ');
                const type = a.type ?? a.applicationType ?? '—';
                return (
                  <tr key={a.applicationNumber}>
                    <td><code style={{ fontSize: '0.82rem' }}>{a.applicationNumber}</code></td>
                    <td><span className={`badge ${type === 'DL' ? 'badge-info' : 'badge-pending'}`}>{type}</span></td>
                    <td>{name || '—'}</td>
                    <td style={{ fontSize: '0.82rem', color: '#6b7280' }}>{a.applicant?.email || '—'}</td>
                    <td><span className={`badge ${badgeClass[a.status] ?? 'badge-info'}`}>{a.status}</span></td>
                    <td>₹{a.amountPaid ?? '—'}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      <div style={{ fontSize: '0.78rem', color: '#9ca3af', marginTop: 8 }}>
        Showing {shown.length} of {apps.length} total applications.
      </div>
    </AppLayout>
  );
}
