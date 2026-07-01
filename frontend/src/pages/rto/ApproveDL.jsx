import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { approveDL } from '../../api/api';

export default function ApproveDL() {
  const [appNo, setAppNo]   = useState('');
  const [msg, setMsg]       = useState('');
  const [error, setError]   = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setMsg(''); setError('');
    try {
      const res = await approveDL(appNo.trim());
      setMsg(res.data.message || 'Driving License approved successfully.');
      setAppNo('');
    } catch (err) {
      setError(err.response?.data?.message || 'Could not approve the DL application.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Approve Driving License" subtitle="US-010 · Approve a DL application">
      <div className="page-header">
        <h1>Approve Driving License</h1>
        <p>Enter the DL application number and confirm approval after successful test.</p>
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
            <div className="info-banner" style={{ marginTop: 12 }}>
              ℹ️ &nbsp; Only approve applications where the driving test has been <strong>passed</strong>.
            </div>
            <hr className="divider" />
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button type="submit" className="btn btn-success" disabled={loading}>
                {loading ? <><span className="spinner" /> Processing...</> : '🟢 Approve DL Application'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AppLayout>
  );
}
