import { useEffect, useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { getAllApplications, updateApplicationDetails } from '../../api/api';

const badgeClass = { PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected' };

export default function ManageApplications() {
  const [apps, setApps] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [filter, setFilter] = useState('ALL');
  const [selectedApp, setSelectedApp] = useState(null);
  const [form, setForm] = useState({
    status: '',
    remarks: '',
    amountPaid: '',
    paymentStatus: '',
    testResult: '',
    applicantName: '',
    email: '',
    phone: ''
  });
  const [saving, setSaving] = useState(false);

  const load = () => {
    setLoading(true);
    getAllApplications()
      .then(r => setApps(r.data))
      .catch(() => setError('Failed to load applications.'))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const shown = filter === 'ALL' ? apps : apps.filter(a => (a.type ?? a.applicationType) === filter);

  const openEditor = (app) => {
    setSelectedApp(app);
    setForm({
      status: app.status ?? '',
      remarks: app.remarks ?? '',
      amountPaid: app.amountPaid ?? '',
      paymentStatus: app.paymentStatus ?? '',
      testResult: app.testResult ?? '',
      applicantName: app.applicant?.fullName ?? '',
      email: app.applicant?.email ?? '',
      phone: app.applicant?.phone ?? ''
    });
    setError('');
    setMessage('');
  };

  const closeEditor = () => {
    setSelectedApp(null);
    setSaving(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (!selectedApp) return;

    setSaving(true);
    try {
      const payload = {
        status: form.status || null,
        remarks: form.remarks,
        amountPaid: Number(form.amountPaid) || 0,
        paymentStatus: form.paymentStatus || null,
        testResult: form.testResult || null,
        applicant: {
          fullName: form.applicantName || null,
          email: form.email || null,
          phone: form.phone || null
        }
      };

      await updateApplicationDetails(selectedApp.applicationNumber, payload);
      setMessage('Application details were updated successfully.');
      closeEditor();
      load();
    } catch {
      setError('Failed to update application details.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <AppLayout title="Manage Applications" subtitle="US-012 · View and edit submitted applications">
      <div className="page-header">
        <h1>All Applications</h1>
        <p>View every LL and DL application submitted on the portal and update key details when needed.</p>
      </div>

      {error && <div className="alert alert-danger">⚠ {error}</div>}
      {message && <div className="alert alert-success">✅ {message}</div>}

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
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {shown.map(a => {
                const name = [a.applicant?.firstName, a.applicant?.middleName, a.applicant?.lastName].filter(Boolean).join(' ') || a.applicant?.fullName || '—';
                const type = a.type ?? a.applicationType ?? '—';
                return (
                  <tr key={a.applicationNumber}>
                    <td><code style={{ fontSize: '0.82rem' }}>{a.applicationNumber}</code></td>
                    <td><span className={`badge ${type === 'DL' ? 'badge-info' : 'badge-pending'}`}>{type}</span></td>
                    <td>{name}</td>
                    <td style={{ fontSize: '0.82rem', color: '#6b7280' }}>{a.applicant?.email || '—'}</td>
                    <td><span className={`badge ${badgeClass[a.status] ?? 'badge-info'}`}>{a.status}</span></td>
                    <td>₹{a.amountPaid ?? '—'}</td>
                    <td>
                      <button className="btn btn-outline" style={{ fontSize: '0.8rem', padding: '6px 10px' }} onClick={() => openEditor(a)}>
                        ✏️ Edit
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {selectedApp && (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(15, 23, 42, 0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: 16 }}>
          <div style={{ background: '#fff', width: '100%', maxWidth: 720, borderRadius: 16, padding: 24, boxShadow: '0 16px 50px rgba(0,0,0,0.25)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <div>
                <h3 style={{ margin: 0 }}>Edit Application Details</h3>
                <p style={{ margin: '4px 0 0', color: '#6b7280' }}>Application No. {selectedApp.applicationNumber}</p>
              </div>
              <button className="btn btn-outline" onClick={closeEditor}>✕ Close</button>
            </div>

            <form onSubmit={handleSave} style={{ display: 'grid', gap: 12 }}>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 12 }}>
                <label>
                  <div style={{ fontSize: '0.85rem', marginBottom: 4 }}>Status</div>
                  <select name="status" value={form.status} onChange={handleChange} className="input" style={{ width: '100%' }}>
                    <option value="">Select status</option>
                    <option value="PENDING">PENDING</option>
                    <option value="APPROVED">APPROVED</option>
                    <option value="REJECTED">REJECTED</option>
                  </select>
                </label>
                <label>
                  <div style={{ fontSize: '0.85rem', marginBottom: 4 }}>Payment Status</div>
                  <input name="paymentStatus" value={form.paymentStatus} onChange={handleChange} className="input" style={{ width: '100%' }} placeholder="PAID / PENDING" />
                </label>
                <label>
                  <div style={{ fontSize: '0.85rem', marginBottom: 4 }}>Amount Paid</div>
                  <input name="amountPaid" type="number" step="0.01" value={form.amountPaid} onChange={handleChange} className="input" style={{ width: '100%' }} />
                </label>
                <label>
                  <div style={{ fontSize: '0.85rem', marginBottom: 4 }}>Test Result</div>
                  <select name="testResult" value={form.testResult} onChange={handleChange} className="input" style={{ width: '100%' }}>
                    <option value="">Not set</option>
                    <option value="PASS">PASS</option>
                    <option value="FAIL">FAIL</option>
                  </select>
                </label>
              </div>

              <label>
                <div style={{ fontSize: '0.85rem', marginBottom: 4 }}>Remarks</div>
                <textarea name="remarks" value={form.remarks} onChange={handleChange} className="input" style={{ width: '100%', minHeight: 90 }} placeholder="Add remarks for the application" />
              </label>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 12 }}>
                <label>
                  <div style={{ fontSize: '0.85rem', marginBottom: 4 }}>Applicant Name</div>
                  <input name="applicantName" value={form.applicantName} onChange={handleChange} className="input" style={{ width: '100%' }} />
                </label>
                <label>
                  <div style={{ fontSize: '0.85rem', marginBottom: 4 }}>Email</div>
                  <input name="email" type="email" value={form.email} onChange={handleChange} className="input" style={{ width: '100%' }} />
                </label>
                <label>
                  <div style={{ fontSize: '0.85rem', marginBottom: 4 }}>Phone</div>
                  <input name="phone" value={form.phone} onChange={handleChange} className="input" style={{ width: '100%' }} />
                </label>
              </div>

              <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 8 }}>
                <button type="button" className="btn btn-outline" onClick={closeEditor}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Saving...' : 'Save Changes'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div style={{ fontSize: '0.78rem', color: '#9ca3af', marginTop: 8 }}>
        Showing {shown.length} of {apps.length} total applications.
      </div>
    </AppLayout>
  );
}
