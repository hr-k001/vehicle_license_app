import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { applyLL } from '../../api/api';
import { useAuth } from '../../context/AuthContext';

const emptyForm = (email) => ({
  fullName: '', phone: '', email: email || '', address: '', aadhaarNumber: '', dateOfBirth: '', vehicleType: ''
});

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

export default function ApplyLL() {
  const { user } = useAuth();
  const [form, setForm] = useState(emptyForm(user?.email));
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

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
        modeOfPayment: 'Online', amountPaid: 200, paymentStatus: 'PAID'
      };
      const res = await applyLL(payload);
      setResult(res.data);
      setForm(emptyForm(user?.email));
    } catch (err) {
      setError(getErrorMessage(err));
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
