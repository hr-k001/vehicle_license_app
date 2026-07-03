import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { applyLL } from '../../api/api';
import { useAuth } from '../../context/AuthContext';
import { updateJourneyProgress } from '../../utils/journeyProgress';

const emptyForm = (email) => ({
  fullName: '', phone: '', email: email || '', address: '', aadhaarNumber: '', dateOfBirth: '', vehicleType: ''
});

export default function ApplyLL() {
  const { user } = useAuth();
  const [form, setForm] = useState(emptyForm(user?.email));
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});

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
      const payload = {
        applicant: { ...form },
        modeOfPayment: 'Online', amountPaid: 200, paymentStatus: 'PAID'
      };
      const res = await applyLL(payload);
      setResult(res.data);
      setForm(emptyForm(user?.email));
      if (user?.email) {
        updateJourneyProgress(user.email, { applyLL: true });
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Submission failed. Please check your details.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Apply for Learner License" subtitle="US-003 · Fill the form to submit your application">
      <div className="page-header">
        <h1>Learner License Application</h1>
        <p>Fill in your personal details to apply for a Learner License (LL).</p>
      </div>

      {result && (
        <div className="alert alert-success">
          ✓ Application submitted successfully!
          <span style={{ marginLeft: 8, fontWeight: 700 }}>No: {result.applicationNumber}</span>
          — Save this number to track your status.
        </div>
      )}
      {error && <div className="alert alert-danger">⚠ {error}</div>}

      <div className="card">
        <div className="card-body">
          <div className="card-title">Personal Information</div>
          <div className="card-sub">All fields marked are required</div>

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
              <button type="button" className="btn btn-outline" onClick={() => { setForm(emptyForm(user?.email)); setResult(null); setError(''); }}>
                Clear
              </button>
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? <><span className="spinner" /> Submitting...</> : '📋 Submit Application'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AppLayout>
  );
}
