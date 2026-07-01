import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { applyLL } from '../../api/api';

const empty = {
  firstName: '', middleName: '', lastName: '', mobile: '',
  email: '', placeOfBirth: '', qualification: '', nationality: '', vehicleType: ''
};

export default function ApplyLL() {
  const [form, setForm] = useState(empty);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setResult(null);
    try {
      const payload = {
        applicant: { ...form },
        modeOfPayment: 'Online', amountPaid: 200, paymentStatus: 'PAID'
      };
      const res = await applyLL(payload);
      setResult(res.data);
      setForm(empty);
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
                <label>Place of Birth *</label>
                <input placeholder="e.g. Delhi" value={form.placeOfBirth} onChange={e => set('placeOfBirth', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Qualification *</label>
                <input placeholder="e.g. Graduate" value={form.qualification} onChange={e => set('qualification', e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Nationality *</label>
                <input placeholder="e.g. Indian" value={form.nationality} onChange={e => set('nationality', e.target.value)} required />
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
            </div>

            <hr className="divider" />
            <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
              <button type="button" className="btn btn-outline" onClick={() => { setForm(empty); setResult(null); setError(''); }}>
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
