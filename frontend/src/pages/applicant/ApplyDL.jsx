import { useState, useEffect } from 'react';
import AppLayout from '../../components/AppLayout';
import { applyDL, checkLLByEmail } from '../../api/api';
import { useAuth } from '../../context/AuthContext';
import { Link } from 'react-router-dom';

const empty = {
  firstName: '', middleName: '', lastName: '', mobile: '',
  email: '', placeOfBirth: '', qualification: '', nationality: '', vehicleType: ''
};

export default function ApplyDL() {
  const { user } = useAuth();
  const [llStatus, setLlStatus] = useState(null); // null=checking, 'APPROVED'|'PENDING'|'REJECTED'|'NONE'
  const [llAppNo, setLlAppNo]   = useState('');
  const [form, setForm]         = useState({ ...empty, email: user?.email || '' });
  const [result, setResult]     = useState(null);
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState('');

  useEffect(() => {
    if (!user?.email) { setLlStatus('NONE'); return; }
    checkLLByEmail(user.email)
      .then(r => {
        setLlStatus(r.data.status || 'NONE');
        setLlAppNo(r.data.applicationNumber || '');
      })
      .catch(() => setLlStatus('NONE'));
  }, [user]);

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setResult(null);
    try {
      const payload = { applicant: { ...form }, modeOfPayment: 'Online', amountPaid: 500, paymentStatus: 'PAID' };
      const res = await applyDL(payload);
      setResult(res.data);
      setForm({ ...empty, email: user?.email || '' });
    } catch (err) {
      setError(err.response?.data?.message || 'Submission failed.');
    } finally { setLoading(false); }
  };

  // Still checking LL status
  if (llStatus === null) {
    return (
      <AppLayout title="Apply for Driving License" subtitle="US-007 · DL application">
        <div className="empty-state">Checking your Learner License status…</div>
      </AppLayout>
    );
  }

  // Blocked — no approved LL
  if (llStatus !== 'APPROVED') {
    return (
      <AppLayout title="Apply for Driving License" subtitle="US-007 · DL application">
        <div className="page-header">
          <h1>Driving License Application</h1>
          <p>Prerequisite check before you can apply.</p>
        </div>
        <div className="block-banner">
          <div className="block-banner-icon">🚫</div>
          <h3>Approved Learner License Required</h3>
          <p>
            {llStatus === 'NONE'
              ? 'You have not applied for a Learner License yet. Please apply for an LL first and wait for RTO approval before applying for a Driving License.'
              : llStatus === 'PENDING'
              ? `Your Learner License application (${llAppNo}) is currently pending. Please wait for the RTO officer to approve it.`
              : `Your Learner License application was rejected. Please re-apply for an LL and get it approved first.`}
          </p>
          <Link to="/app/apply-ll" className="btn btn-blue" style={{ marginTop: 8 }}>Apply for Learner License →</Link>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Apply for Driving License" subtitle="US-007 · Submit your DL application">
      <div className="page-header">
        <h1>Driving License Application</h1>
        <p>Your Learner License is approved. You may now apply for a Driving License.</p>
      </div>

      <div className="info-banner">
        ✅ &nbsp; Learner License <strong>{llAppNo}</strong> is <strong>APPROVED</strong>.
        &nbsp; You are eligible to apply for a Driving License.
      </div>

      {result && (
        <div className="alert alert-success">
          ✓ DL Application submitted!
          <span style={{ marginLeft: 8, fontWeight: 700 }}>No: {result.applicationNumber}</span>
          — Schedule your driving test next.
        </div>
      )}
      {error && <div className="alert alert-danger">⚠ {error}</div>}

      <div className="form-card">
        <div className="form-card-header">
          <div className="form-card-header-icon">🚗</div>
          <div>
            <div className="form-card-header-title">Applicant Details</div>
            <div className="form-card-header-sub">Fee: ₹500 · Payment mode: Online</div>
          </div>
        </div>
        <div className="form-card-body">
          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              <div className="form-group">
                <label>First Name *</label>
                <input placeholder="e.g. Himanshu" value={form.firstName} onChange={e => set('firstName', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Middle Name</label>
                <input placeholder="Optional" value={form.middleName} onChange={e => set('middleName', e.target.value)} />
              </div>
              <div className="form-group">
                <label>Last Name *</label>
                <input placeholder="e.g. Kumar" value={form.lastName} onChange={e => set('lastName', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Mobile Number *</label>
                <input placeholder="e.g. 9876543210" value={form.mobile} onChange={e => set('mobile', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Email Address *</label>
                <input type="email" placeholder="you@example.com" value={form.email} onChange={e => set('email', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Vehicle Type *</label>
                <select value={form.vehicleType} onChange={e => set('vehicleType', e.target.value)} required>
                  <option value="">Select vehicle type</option>
                  <option>Two Wheeler</option>
                  <option>Four Wheeler</option>
                  <option>Heavy Motor Vehicle</option>
                </select>
              </div>
              <div className="form-group">
                <label>Nationality *</label>
                <input placeholder="e.g. Indian" value={form.nationality} onChange={e => set('nationality', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Place of Birth</label>
                <input placeholder="e.g. Delhi" value={form.placeOfBirth} onChange={e => set('placeOfBirth', e.target.value)} />
              </div>
            </div>
            <hr className="divider" />
            <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
              <button type="button" className="btn btn-outline" onClick={() => { setForm({ ...empty, email: user?.email || '' }); setResult(null); setError(''); }}>Clear</button>
              <button type="submit" className="btn btn-blue" disabled={loading}>
                {loading ? <><span className="spinner" /> Submitting...</> : '🚗 Submit DL Application'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AppLayout>
  );
}
