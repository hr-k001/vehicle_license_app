import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { getLLStatus } from '../../api/api';
import { useAuth } from '../../context/AuthContext';
import { updateJourneyProgress } from '../../utils/journeyProgress';

const badgeClass = { PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected' };
const statusIcon = { PENDING: '⏳', APPROVED: '✅', REJECTED: '❌' };
const statusMsg  = {
  PENDING:  'Your application is under review by the RTO officer.',
  APPROVED: 'Congratulations! Your Learner License has been approved.',
  REJECTED: 'Your application was rejected. Please contact the RTO office for details.',
};

export default function ViewLLStatus() {
  const { user } = useAuth();
  const [appNo, setAppNo] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleCheck = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setResult(null);
    try {
      const res = await getLLStatus(appNo.trim());
      setResult(res.data);
      if (user?.email) {
        updateJourneyProgress(user.email, {
          applyLL: true,
          llApproval: res.data?.status === 'APPROVED',
        });
      }
    } catch (err) {
      if (err.response?.status === 404) setError('No application found with that number.');
      else setError('Could not fetch status. Ensure the backend is running.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Learner License Status" subtitle="US-004 · Track your LL application">
      <div className="page-header">
        <h1>Check LL Application Status</h1>
        <p>Enter your application number to see the current status.</p>
      </div>

      <div className="card">
        <div className="card-body">
          <form onSubmit={handleCheck}>
            <div className="form-group">
              <label>Application Number</label>
              <input placeholder="e.g. LL-1001" value={appNo}
                onChange={e => { setAppNo(e.target.value); setError(''); setResult(null); }} required />
            </div>
            {error && <div className="alert alert-danger">⚠ {error}</div>}
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? <><span className="spinner" /> Checking...</> : '🔍 Check Status'}
            </button>
          </form>

          {result && (
            <div className="result-box" style={{ marginTop: 24 }}>
              <h4>Application Status</h4>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
                <span style={{ fontSize: '2rem' }}>{statusIcon[result.status]}</span>
                <div>
                  <span className={`badge ${badgeClass[result.status]}`}>{result.status}</span>
                  <div className="sub" style={{ marginTop: 6 }}>{statusMsg[result.status]}</div>
                </div>
              </div>
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
