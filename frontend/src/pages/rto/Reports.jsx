import { useEffect, useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { getReportApplications, getReportCounts } from '../../api/api';

const tabs = ['all', 'pending', 'approved', 'rejected'];
const badgeClass = { PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected' };

function formatDate(value) {
  if (!value) return '-';
  return new Date(value).toLocaleDateString('en-IN');
}

export default function Reports() {
  const [counts, setCounts] = useState(null);
  const [rows, setRows] = useState([]);
  const [tab, setTab] = useState('all');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = (nextTab = tab) => {
    setLoading(true);
    setError('');
    Promise.all([getReportCounts(), getReportApplications(nextTab)])
      .then(([countRes, rowsRes]) => {
        setCounts(countRes.data);
        setRows(rowsRes.data || []);
      })
      .catch(() => setError('Failed to load reports.'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load('all'); }, []);

  const switchTab = (nextTab) => {
    setTab(nextTab);
    load(nextTab);
  };

  const stats = counts ? [
    ['Total', counts.totalApplications],
    ['Pending', counts.pendingCount],
    ['Approved', counts.approvedCount],
    ['Rejected', counts.rejectedCount],
    ['LL', counts.learnerLicenseCount],
    ['DL', counts.drivingLicenseCount],
  ] : [];

  return (
    <AppLayout title="Reports" subtitle="US-018 - Application reports">
      <div className="page-header">
        <h1>Application Reports</h1>
        <p>View application counts and filtered report data.</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="stats-row" style={{ marginBottom: 24 }}>
        {stats.map(([label, value]) => (
          <div className="stat-card" key={label}>
            <div className="stat-value">{value}</div>
            <div className="stat-label">{label}</div>
          </div>
        ))}
      </div>

      <div className="filter-tabs">
        {tabs.map((t) => (
          <button key={t} className={`filter-tab${tab === t ? ' active' : ''}`} onClick={() => switchTab(t)}>
            {t.toUpperCase()}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="empty-state">Loading reports...</div>
      ) : rows.length === 0 ? (
        <div className="empty-state">No report rows found.</div>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Application No.</th>
                <th>Applicant</th>
                <th>Email</th>
                <th>Type</th>
                <th>Status</th>
                <th>Application Date</th>
                <th>Test Result</th>
                <th>Payment</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.applicationNumber}>
                  <td><code>{r.applicationNumber}</code></td>
                  <td>{r.applicantName}</td>
                  <td>{r.applicantEmail}</td>
                  <td>{r.type}</td>
                  <td><span className={`badge ${badgeClass[r.status] || 'badge-info'}`}>{r.status}</span></td>
                  <td>{formatDate(r.applicationDate)}</td>
                  <td>{r.testResult}</td>
                  <td>{r.paymentStatus} - Rs.{r.amountPaid}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </AppLayout>
  );
}
