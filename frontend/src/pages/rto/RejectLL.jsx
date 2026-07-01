import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { rejectLL } from '../../api/api';

export default function RejectLL() {
  const [appNo, setAppNo]   = useState('');
  const [msg, setMsg]       = useState('');
  const [error, setError]   = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setMsg(''); setError('');
    try {
      const res = await rejectLL(appNo.trim());
      setMsg(res.data.message || 'Learner License application rejected.');
      setAppNo('');
    } catch (err) {
      setError(err.response?.data?.message || 'Could not reject the application.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Reject Learner License" subtitle="US-006 · Reject an LL application">
      <div className="page-header">
        <h1>Reject Learner License Application</h1>
        <p>Enter the LL application number to mark it as rejected.</p>
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
            <div className="info-banner" style={{ marginTop: 12, borderColor: '#fca5a5', background: '#fef2f2' }}>
              ⚠️ &nbsp; This action will mark the application as <strong>Rejected</strong>. The applicant will need to re-apply.
            </div>
            <hr className="divider" />
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button type="submit" className="btn btn-danger" disabled={loading}>
                {loading ? <><span className="spinner" /> Processing...</> : '❌ Reject Application'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AppLayout>
  );
}
