import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { rejectDL } from '../../api/api';

export default function RejectDL() {
  const [appNo, setAppNo]   = useState('');
  const [msg, setMsg]       = useState('');
  const [error, setError]   = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setMsg(''); setError('');
    try {
      const res = await rejectDL(appNo.trim());
      setMsg(res.data.message || 'DL application rejected.');
      setAppNo('');
    } catch (err) {
      setError(err.response?.data?.message || 'Could not reject the DL application.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Reject Driving License" subtitle="US-011 · Reject a DL application">
      <div className="page-header">
        <h1>Reject Driving License Application</h1>
        <p>Enter the DL application number to mark it as rejected.</p>
      </div>

      {msg   && <div className="alert alert-success">✓ {msg}</div>}
      {error && <div className="alert alert-danger">⚠ {error}</div>}

      <div className="card">
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>DL Application Number *</label>
              <input placeholder="e.g. DL-2001" value={appNo}
                onChange={e => { setAppNo(e.target.value); setMsg(''); setError(''); }} required />
            </div>
            <div className="info-banner" style={{ marginTop: 12, borderColor: '#fca5a5', background: '#fef2f2' }}>
              ⚠️ &nbsp; This action is <strong>irreversible</strong>. The applicant will need to re-apply for a DL.
            </div>
            <hr className="divider" />
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button type="submit" className="btn btn-danger" disabled={loading}>
                {loading ? <><span className="spinner" /> Processing...</> : '🔴 Reject DL Application'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AppLayout>
  );
}
