import { useState, useEffect, useCallback } from 'react';
import AppLayout from '../../components/AppLayout';
import { getAllApplications, approveLL, rejectLL } from '../../api/api';

const TABS = ['ALL', 'PENDING', 'APPROVED', 'REJECTED'];

function statusBadge(status) {
  const map = { APPROVED: 'badge-green', REJECTED: 'badge-red', PENDING: 'badge-yellow' };
  return <span className={`badge ${map[status] || 'badge-gray'}`}>{status}</span>;
}

export default function LLApplications() {
  const [apps, setApps]       = useState([]);
  const [tab, setTab]         = useState('PENDING');
  const [search, setSearch]   = useState('');
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionMsg, setActionMsg] = useState('');
  const [actionErr, setActionErr] = useState('');
  const [actioning, setActioning] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    getAllApplications()
      .then(r => {
        const ll = (r.data || []).filter(a => a.type === 'LL');
        setApps(ll);
        if (selected) {
          const fresh = ll.find(a => a.applicationNumber === selected.applicationNumber);
          setSelected(fresh || null);
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []); // eslint-disable-line

  useEffect(() => { load(); }, [load]);

  const filtered = apps.filter(a => {
    const matchTab = tab === 'ALL' || a.status === tab;
    if (!matchTab) return false;
    if (!search.trim()) return true;
    const q = search.toLowerCase();
    const name = `${a.applicant?.firstName || ''} ${a.applicant?.lastName || ''}`.toLowerCase();
    return (
      a.applicationNumber?.toLowerCase().includes(q) ||
      a.applicant?.email?.toLowerCase().includes(q) ||
      name.includes(q)
    );
  });

  const doAction = async (fn, appNo) => {
    setActioning(true); setActionMsg(''); setActionErr('');
    try {
      await fn(appNo);
      setActionMsg('Action completed successfully.');
      load();
    } catch (e) {
      setActionErr(e.response?.data?.message || 'Action failed.');
    } finally { setActioning(false); }
  };

  return (
    <AppLayout title="Learner License Applications" subtitle="US-005 / US-006 · Review and act on LL applications">
      <div className="page-header">
        <h1>Learner License Applications</h1>
        <p>Review pending applications and approve or reject them.</p>
      </div>

      <div className="app-list-layout">
        {/* Left — list */}
        <div className="app-list-panel">
          <div className="list-controls">
            <div className="filter-tabs">
              {TABS.map(t => (
                <button key={t} className={`filter-tab${tab === t ? ' active' : ''}`} onClick={() => { setTab(t); setSelected(null); setActionMsg(''); setActionErr(''); }}>
                  {t}
                  <span className="tab-count">{t === 'ALL' ? apps.length : apps.filter(a => a.status === t).length}</span>
                </button>
              ))}
            </div>
            <input
              className="search-input"
              placeholder="Search by name, email, or app no…"
              value={search}
              onChange={e => { setSearch(e.target.value); setSelected(null); }}
            />
          </div>

          {loading ? (
            <div className="empty-state">Loading…</div>
          ) : filtered.length === 0 ? (
            <div className="empty-state">No applications found.</div>
          ) : (
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>App No.</th>
                    <th>Applicant</th>
                    <th>Email</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {filtered.map(a => (
                    <tr
                      key={a.applicationNumber}
                      className={`clickable${selected?.applicationNumber === a.applicationNumber ? ' selected' : ''}`}
                      onClick={() => { setSelected(a); setActionMsg(''); setActionErr(''); }}
                    >
                      <td><code>{a.applicationNumber}</code></td>
                      <td>{a.applicant?.firstName} {a.applicant?.lastName}</td>
                      <td>{a.applicant?.email}</td>
                      <td>{statusBadge(a.status)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Right — detail */}
        {selected ? (
          <div className="app-detail-panel">
            <div className="detail-panel-header">
              <div>
                <div className="detail-panel-title">{selected.applicant?.firstName} {selected.applicant?.lastName}</div>
                <div className="detail-panel-sub">{selected.applicationNumber}</div>
              </div>
              <button className="btn-close-panel" onClick={() => { setSelected(null); setActionMsg(''); setActionErr(''); }}>✕</button>
            </div>

            <div className="detail-body">
              <div className="detail-section-title">Applicant Information</div>
              <div className="detail-grid">
                <div className="detail-row"><span>Full Name</span><strong>{selected.applicant?.firstName} {selected.applicant?.middleName || ''} {selected.applicant?.lastName}</strong></div>
                <div className="detail-row"><span>Email</span><strong>{selected.applicant?.email}</strong></div>
                <div className="detail-row"><span>Mobile</span><strong>{selected.applicant?.mobile}</strong></div>
                <div className="detail-row"><span>Nationality</span><strong>{selected.applicant?.nationality}</strong></div>
                <div className="detail-row"><span>Place of Birth</span><strong>{selected.applicant?.placeOfBirth || '—'}</strong></div>
                <div className="detail-row"><span>Qualification</span><strong>{selected.applicant?.qualification || '—'}</strong></div>
              </div>

              <div className="detail-section-title" style={{ marginTop: 20 }}>Application Details</div>
              <div className="detail-grid">
                <div className="detail-row"><span>Application No.</span><strong>{selected.applicationNumber}</strong></div>
                <div className="detail-row"><span>Status</span><strong>{statusBadge(selected.status)}</strong></div>
                <div className="detail-row"><span>Payment</span><strong>{selected.paymentStatus} — ₹{selected.amountPaid}</strong></div>
                <div className="detail-row"><span>Mode</span><strong>{selected.modeOfPayment}</strong></div>
              </div>
            </div>

            {actionMsg && <div className="alert alert-success" style={{ margin: '12px 20px 0' }}>✓ {actionMsg}</div>}
            {actionErr && <div className="alert alert-danger"  style={{ margin: '12px 20px 0' }}>⚠ {actionErr}</div>}

            {selected.status === 'PENDING' && (
              <div className="detail-actions">
                <button className="btn btn-success" disabled={actioning} onClick={() => doAction(approveLL, selected.applicationNumber)}>
                  {actioning ? '…' : '✅ Approve'}
                </button>
                <button className="btn btn-danger" disabled={actioning} onClick={() => doAction(rejectLL, selected.applicationNumber)}>
                  {actioning ? '…' : '❌ Reject'}
                </button>
              </div>
            )}
            {selected.status !== 'PENDING' && (
              <div className="detail-actions">
                <span className="detail-status-note">
                  {selected.status === 'APPROVED' ? '✅ This application has been approved.' : '❌ This application has been rejected.'}
                </span>
              </div>
            )}
          </div>
        ) : (
          <div className="app-detail-panel detail-placeholder">
            <div className="placeholder-icon">📋</div>
            <p>Select an application from the list to view its details and take action.</p>
          </div>
        )}
      </div>
    </AppLayout>
  );
}
