import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { getDLStatus } from '../../api/api';

const badgeClass = { PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected', SCHEDULED: 'badge-info' };
const statusIcon = { PENDING: '⏳', APPROVED: '✅', REJECTED: '❌', SCHEDULED: '📅' };
const statusMsg  = {
  PENDING:   'Your DL application is pending RTO officer review.',
  SCHEDULED: 'Your driving test is scheduled. Please attend on the confirmed date.',
  APPROVED:  'Congratulations! Your Driving License has been approved.',
  REJECTED:  'Your DL application was rejected. Contact the RTO office for assistance.',
};

export default function ViewDLStatus() {
  const [appNo, setAppNo]   = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError]   = useState('');

  const handleCheck = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setResult(null);
    try {
      const res = await getDLStatus(appNo.trim());
      setResult(res.data);
    } catch (err) {
      if (err.response?.status === 404) setError('No DL application found with that number.');
      else setError('Could not fetch status. Ensure the backend is running.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Driving License Status" subtitle="US-009 · Track your DL application">
      <div className="page-header">
        <h1>Check DL Application Status</h1>
        <p>Enter your DL application number to see the current status.</p>
      </div>

      <div className="card">
        <div className="card-body">
          <form onSubmit={handleCheck}>
            <div className="form-group">
              <label>DL Application Number</label>
              <input placeholder="e.g. DL-2001" value={appNo}
                onChange={e => { setAppNo(e.target.value); setError(''); setResult(null); }} required />
            </div>
            {error && <div className="alert alert-danger">⚠ {error}</div>}
            <button type="submit" className="btn btn-blue" disabled={loading}>
              {loading ? <><span className="spinner" /> Checking...</> : '🔍 Check Status'}
            </button>
          </form>

          {result && (
            <div className="result-box" style={{ marginTop: 24 }}>
              <h4>DL Application Status</h4>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
                <span style={{ fontSize: '2rem' }}>{statusIcon[result.status] ?? '🔵'}</span>
                <div>
                  <span className={`badge ${badgeClass[result.status] ?? 'badge-info'}`}>{result.status}</span>
                  <div className="sub" style={{ marginTop: 6 }}>{statusMsg[result.status] ?? result.status}</div>
                </div>
              </div>
              {result.testDate && (
                <div style={{ marginTop: 8, padding: '8px 12px', background: '#eff6ff', borderRadius: 6, fontSize: '0.83rem' }}>
                  Test Date: <strong>{new Date(result.testDate).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })}</strong>
                </div>
              )}
              <div style={{ fontSize: '0.8rem', color: '#9ca3af', marginTop: 8 }}>
                Application No: <strong>{result.applicationNumber}</strong>
              </div>
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
