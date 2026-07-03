import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { scheduleTest } from '../../api/api';
import { useAuth } from '../../context/AuthContext';
import { updateJourneyProgress } from '../../utils/journeyProgress';

export default function ScheduleTest() {
  const { user } = useAuth();
  const [appNo, setAppNo]   = useState('');
  const [testDate, setDate] = useState('');
  const [msg, setMsg]       = useState('');
  const [error, setError]   = useState('');
  const [loading, setLoading] = useState(false);

  const minDate = new Date();
  minDate.setDate(minDate.getDate() + 3);
  const minStr = minDate.toISOString().split('T')[0];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setMsg(''); setError('');
    try {
      const res = await scheduleTest(appNo.trim(), { testDate });
      setMsg(res.data.message);
      if (user?.email) {
        updateJourneyProgress(user.email, { bookTest: true, applyDL: true });
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Could not book the test slot.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Schedule Driving Test" subtitle="US-008 · Book your test slot">
      <div className="page-header">
        <h1>Book Driving Test Slot</h1>
        <p>Enter your DL Application Number and choose a preferred test date.</p>
      </div>

      <div className="info-banner">
        📅 &nbsp; Test slots must be booked at least <strong>3 days</strong> in advance. Please bring your original documents on the test day.
      </div>

      {msg   && <div className="alert alert-success">✓ {msg}</div>}
      {error && <div className="alert alert-danger">⚠ {error}</div>}

      <div className="card">
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              <div className="form-group">
                <label>DL Application Number *</label>
                <input placeholder="e.g. DL-2001" value={appNo}
                  onChange={e => { setAppNo(e.target.value); setMsg(''); setError(''); }} required />
              </div>
              <div className="form-group">
                <label>Preferred Test Date *</label>
                <input type="date" min={minStr} value={testDate}
                  onChange={e => { setDate(e.target.value); setMsg(''); setError(''); }} required />
              </div>
            </div>
            <hr className="divider" />
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? <><span className="spinner" /> Booking...</> : '📅 Confirm Test Slot'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AppLayout>
  );
}
