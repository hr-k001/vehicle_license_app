import { useState, useEffect } from 'react';
import AppLayout from '../../components/AppLayout';
import { applyDL, checkLLByEmail, getLLStatus } from '../../api/api';
import { useAuth } from '../../context/AuthContext';
import { Link } from 'react-router-dom';
import { updateJourneyProgress } from '../../utils/journeyProgress';

const empty = {
  fullName: '', phone: '', email: '', address: '', aadhaarNumber: '', dateOfBirth: '', vehicleType: ''
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
  const [fieldErrors, setFieldErrors] = useState({});

  useEffect(() => {
    if (!user?.email) { setLlStatus('NONE'); return; }
    checkLLByEmail(user.email)
      .then(r => {
        const status = r.data.status || 'NONE';
        setLlStatus(status);
        setLlAppNo(r.data.applicationNumber || '');
      })
      .catch(() => setLlStatus('NONE'));
  }, [user]);

  useEffect(() => {
    if (user?.email && llStatus === 'APPROVED') {
      updateJourneyProgress(user.email, { llApproval: true });
    }
  }, [llStatus, user]);

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const getAge = (dobValue) => {
    if (!dobValue) return null;
    const dob = new Date(dobValue);
    const today = new Date();
    let age = today.getFullYear() - dob.getFullYear();
    const monthDiff = today.getMonth() - dob.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
      age -= 1;
    }
    return age;
  };

  const validateForm = (values) => {
    const errors = {};
    if (!values.fullName.trim()) errors.fullName = 'Full Name is required.';
    if (!values.email.trim()) errors.email = 'Email address is required.';
    else if (!/^\S+@\S+\.\S+$/.test(values.email.trim())) errors.email = 'Enter a valid email address.';
    if (!values.phone.trim()) errors.phone = 'Phone number is required.';
    else if (!/^\d{10}$/.test(values.phone.trim())) errors.phone = 'Phone number must be 10 digits.';
    if (!values.address.trim()) errors.address = 'Address is required.';
    if (!values.aadhaarNumber.trim()) errors.aadhaarNumber = 'Aadhaar number is required.';
    else if (!/^\d{12}$/.test(values.aadhaarNumber.trim())) errors.aadhaarNumber = 'Aadhaar number must be 12 digits.';
    if (!values.dateOfBirth) {
      errors.dateOfBirth = 'Date of Birth is required.';
    } else if (getAge(values.dateOfBirth) < 18) {
      errors.dateOfBirth = 'Applicant must be at least 18 years old.';
    }
    if (!values.vehicleType) errors.vehicleType = 'Vehicle Type is required.';
    return errors;
  };

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
        if (user?.email) {
          updateJourneyProgress(user.email, { llApproval: true });
        }
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
    const validationErrors = validateForm(form);
    if (Object.keys(validationErrors).length > 0) {
      setFieldErrors(validationErrors);
      setError('Please fix the form errors before submitting.');
      return;
    }
    setLoading(true); setError(''); setResult(null); setFieldErrors({});
    try {
      const payload = { applicant: { ...form }, modeOfPayment: 'Online', amountPaid: 500, paymentStatus: 'PAID' };
      const res = await applyDL(payload);
      setResult(res.data);
      setForm({ ...empty, email: user?.email || '' });
      if (user?.email) {
        updateJourneyProgress(user.email, { applyDL: true });
      }
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
                {fieldErrors.fullName && <div className="field-error">{fieldErrors.fullName}</div>}
              </div>
              <div className="form-group">
                <label>Email Address *</label>
                <input type="email" placeholder="you@example.com" value={form.email} onChange={e => set('email', e.target.value)} required />
                {fieldErrors.email && <div className="field-error">{fieldErrors.email}</div>}
              </div>
              <div className="form-group">
                <label>Phone Number *</label>
                <input placeholder="e.g. 9876543210" value={form.phone} onChange={e => set('phone', e.target.value)} required />
                {fieldErrors.phone && <div className="field-error">{fieldErrors.phone}</div>}
              </div>
              <div className="form-group">
                <label>Address *</label>
                <input placeholder="e.g. 123 Main Street, Delhi" value={form.address} onChange={e => set('address', e.target.value)} required />
                {fieldErrors.address && <div className="field-error">{fieldErrors.address}</div>}
              </div>
              <div className="form-group">
                <label>Aadhaar Number *</label>
                <input placeholder="e.g. 123456789012" value={form.aadhaarNumber} onChange={e => set('aadhaarNumber', e.target.value)} required />
                {fieldErrors.aadhaarNumber && <div className="field-error">{fieldErrors.aadhaarNumber}</div>}
              </div>
              <div className="form-group">
                <label>Date of Birth *</label>
                <input type="date" value={form.dateOfBirth} onChange={e => set('dateOfBirth', e.target.value)} required />
                {fieldErrors.dateOfBirth && <div className="field-error">{fieldErrors.dateOfBirth}</div>}
              </div>
              <div className="form-group">
                <label>Vehicle Type *</label>
                <select value={form.vehicleType} onChange={e => set('vehicleType', e.target.value)} required>
                  <option value="">Select vehicle type</option>
                  <option>Two Wheeler</option>
                  <option>Light Motor Vehicle</option>
                  <option>Heavy Motor Vehicle</option>
                </select>
                {fieldErrors.vehicleType && <div className="field-error">{fieldErrors.vehicleType}</div>}
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
