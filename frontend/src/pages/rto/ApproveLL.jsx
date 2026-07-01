import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { approveLL } from '../../api/api';

export default function ApproveLL() {
  const [appNo, setAppNo]   = useState('');
  const [msg, setMsg]       = useState('');
  const [error, setError]   = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setMsg(''); setError('');
    try {
      const res = await approveLL(appNo.trim());
      setMsg(res.data.message || 'Learner License approved successfully.');
      setAppNo('');
    } catch (err) {
      setError(err.response?.data?.message || 'Could not approve the application.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Approve Learner License" subtitle="US-005 · Approve an LL application">
      <div className="page-header">
        <h1>Approve Learner License</h1>
        <p>Enter the LL application number and confirm approval.</p>
      </div>

      {msg   && <div className="alert alert-success">✓ {msg}</div>}
      {error && <div className="alert alert-danger">⚠ {error}</div>}

      <div className="card">
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>LL Application Number *</label>
              <input placeholder="e.g. LL-1001" value={appNo}
                onChange={e => { setAppNo(e.target.value); setMsg(''); setError(''); }} required />
            </div>
            <div className="info-banner" style={{ marginTop: 12 }}>
              ⚠️ &nbsp; Approving this application will notify the applicant that their Learner License has been granted.
            </div>
            <hr className="divider" />
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button type="submit" className="btn btn-success" disabled={loading}>
                {loading ? <><span className="spinner" /> Processing...</> : '✅ Approve Application'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AppLayout>
  );
}
