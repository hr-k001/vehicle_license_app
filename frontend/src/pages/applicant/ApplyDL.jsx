import { useState, useEffect } from 'react';
import AppLayout from '../../components/AppLayout';
import { applyDL, checkLLByEmail, getLLStatus } from '../../api/api';
import { useAuth } from '../../context/AuthContext';
import { Link } from 'react-router-dom';

const empty = {
  fullName: '', phone: '', email: '', address: '', aadhaarNumber: '', dateOfBirth: '', vehicleType: ''
};

const maxDateOfBirth = () => {
  const date = new Date();
  date.setFullYear(date.getFullYear() - 18);
  return date.toISOString().slice(0, 10);
};

const getErrorMessage = (err) => {
  const data = err.response?.data;
  if (data?.validationErrors) {
    return Object.values(data.validationErrors).join(' ');
  }
  return data?.message || 'Submission failed. Please check your details.';
};

export default function ApplyDL() {
  const { user } = useAuth();
  const [llStatus, setLlStatus]       = useState(null); // null=checking
  const [llAppNo, setLlAppNo]         = useState('');

  // Fallback: manual LL app number entry
  const [manualAppNo, setManualAppNo] = useState('');
  const [manualChecking, setManualChecking] = useState(false);
  const [manualError, setManualError] = useState('');

  const [form, setForm]   = useState({ ...empty, email: user?.email || '' });
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError]   = useState('');

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

  // Fallback: user enters their LL application number manually
  const handleManualVerify = async (e) => {
    e.preventDefault();
    const appNo = manualAppNo.trim().toUpperCase();
    if (!appNo) return;
    setManualChecking(true); setManualError('');
    try {
      const res = await getLLStatus(appNo);
      const status = res.data?.status;
      const fetchedAppNo = res.data?.applicationNumber || appNo;
      if (status === 'APPROVED') {
        setLlStatus('APPROVED');
        setLlAppNo(fetchedAppNo);
      } else if (status === 'PENDING') {
        setManualError(`Application ${appNo} is still PENDING. Please wait for RTO approval.`);
      } else if (status === 'REJECTED') {
        setManualError(`Application ${appNo} was REJECTED. Please re-apply for an LL.`);
      } else {
        setManualError(`Application number "${appNo}" not found. Please check and try again.`);
      }
    } catch {
      setManualError('Could not verify application. Please check the number and try again.');
    } finally { setManualChecking(false); }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setResult(null);
    try {
      const payload = {
        applicant: {
          ...form,
          fullName: form.fullName.trim(),
          email: form.email.trim(),
          phone: form.phone.trim(),
          address: form.address.trim(),
          aadhaarNumber: form.aadhaarNumber.trim()
        },
        modeOfPayment: 'Online',
        amountPaid: 500,
        paymentStatus: 'PAID'
      };
      const res = await applyDL(payload);
      setResult(res.data);
      setForm({ ...empty, email: user?.email || '' });
    } catch (err) {
      setError(getErrorMessage(err));
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

  // Blocked — email check didn't find an approved LL
  if (llStatus !== 'APPROVED') {
    return (
      <AppLayout title="Apply for Driving License" subtitle="US-007 · DL application">
        <div className="page-header">
          <h1>Driving License Application</h1>
          <p>Prerequisite check before you can apply.</p>
        </div>

        {llStatus === 'PENDING' ? (
          <div className="block-banner">
            <div className="block-banner-icon">⏳</div>
            <h3>LL Application Pending</h3>
            <p>Your Learner License application ({llAppNo}) is currently pending. Please wait for the RTO officer to approve it.</p>
          </div>
        ) : llStatus === 'REJECTED' ? (
          <div className="block-banner">
            <div className="block-banner-icon">❌</div>
            <h3>LL Application Rejected</h3>
            <p>Your Learner License application was rejected. Please re-apply for an LL and get it approved first.</p>
            <Link to="/app/apply-ll" className="btn btn-blue" style={{ marginTop: 8 }}>Re-apply for LL →</Link>
          </div>
        ) : (
          /* NONE — auto email check found nothing; offer manual verification fallback */
          <>
            <div className="block-banner">
              <div className="block-banner-icon">🚫</div>
              <h3>Approved Learner License Required</h3>
              <p>
                No approved Learner License was found linked to your account email.
                If you applied for an LL using a different email address, enter your LL application number below to verify.
              </p>
              <Link to="/app/apply-ll" className="btn btn-blue" style={{ marginTop: 8 }}>Apply for LL →</Link>
            </div>

            <div className="form-card" style={{ marginTop: 20 }}>
              <div className="form-card-header">
                <div className="form-card-header-icon">🔑</div>
                <div>
                  <div className="form-card-header-title">Already have an LL application?</div>
                  <div className="form-card-header-sub">Enter your application number to verify its status</div>
                </div>
              </div>
              <div className="form-card-body">
                <form onSubmit={handleManualVerify}>
                  <div className="form-grid" style={{ gridTemplateColumns: '1fr auto' }}>
                    <div className="form-group" style={{ marginBottom: 0 }}>
                      <label>LL Application Number</label>
                      <input
                        placeholder="e.g. LL-1001"
                        value={manualAppNo}
                        onChange={e => { setManualAppNo(e.target.value); setManualError(''); }}
                        style={{ textTransform: 'uppercase' }}
                        required
                      />
                    </div>
                    <div className="form-group" style={{ marginBottom: 0, justifyContent: 'flex-end', display: 'flex', alignItems: 'flex-end' }}>
                      <button type="submit" className="btn btn-blue" disabled={manualChecking}>
                        {manualChecking ? '…Checking' : '🔍 Verify'}
                      </button>
                    </div>
                  </div>
                  {manualError && <div className="alert alert-danger" style={{ marginTop: 12 }}>⚠ {manualError}</div>}
                </form>
              </div>
            </div>
          </>
        )}
      </AppLayout>
    );
  }

  // LL is APPROVED — show DL application form
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
                <label>Full Name *</label>
                <input placeholder="e.g. Himanshu Kumar" value={form.fullName} onChange={e => set('fullName', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Email Address *</label>
                <input type="email" placeholder="you@example.com" value={form.email} onChange={e => set('email', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Phone Number *</label>
                <input
                  inputMode="numeric"
                  pattern="[0-9]{10}"
                  maxLength="10"
                  title="Phone number must be exactly 10 digits"
                  placeholder="e.g. 9876543210"
                  value={form.phone}
                  onChange={e => set('phone', e.target.value.replace(/\D/g, '').slice(0, 10))}
                  required
                />
              </div>
              <div className="form-group">
                <label>Address *</label>
                <input minLength="5" maxLength="255" placeholder="e.g. 123 Main Street, Delhi" value={form.address} onChange={e => set('address', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Aadhaar Number *</label>
                <input
                  inputMode="numeric"
                  pattern="[0-9]{12}"
                  maxLength="12"
                  title="Aadhaar number must be exactly 12 digits"
                  placeholder="e.g. 123456789012"
                  value={form.aadhaarNumber}
                  onChange={e => set('aadhaarNumber', e.target.value.replace(/\D/g, '').slice(0, 12))}
                  required
                />
              </div>
              <div className="form-group">
                <label>Date of Birth *</label>
                <input type="date" max={maxDateOfBirth()} value={form.dateOfBirth} onChange={e => set('dateOfBirth', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Vehicle Type *</label>
                <select value={form.vehicleType} onChange={e => set('vehicleType', e.target.value)} required>
                  <option value="">Select vehicle type</option>
                  <option>Two Wheeler</option>
                  <option>Light Motor Vehicle</option>
                  <option>Heavy Motor Vehicle</option>
                </select>
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
