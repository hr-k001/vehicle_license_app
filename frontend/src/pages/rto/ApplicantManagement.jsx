import { useEffect, useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { createApplicant, deleteApplicant, getApplicants, updateApplicant } from '../../api/api';

const emptyForm = {
  fullName: '',
  email: '',
  phone: '',
  address: '',
  aadhaarNumber: '',
  dateOfBirth: '',
  learnerLicenseNumber: '',
  drivingLicenseNumber: '',
};

function validationMessage(err) {
  const errors = err.response?.data?.validationErrors;
  if (errors) return Object.values(errors).join(' ');
  return err.response?.data?.message || 'Action failed. Please check the details and try again.';
}

export default function ApplicantManagement() {
  const [applicants, setApplicants] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const load = () => {
    setLoading(true);
    getApplicants()
      .then((res) => setApplicants(res.data || []))
      .catch(() => setError('Failed to load applicant records.'))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const reset = () => {
    setForm(emptyForm);
    setEditingId(null);
    setError('');
    setMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');

    try {
      if (editingId) {
        await updateApplicant(editingId, form);
        setMessage('Applicant record updated successfully.');
      } else {
        await createApplicant(form);
        setMessage('Applicant record created successfully.');
      }
      reset();
      load();
    } catch (err) {
      setError(validationMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const edit = (applicant) => {
    setEditingId(applicant.applicantId);
    setForm({
      fullName: applicant.fullName || '',
      email: applicant.email || '',
      phone: applicant.phone || '',
      address: applicant.address || '',
      aadhaarNumber: applicant.aadhaarNumber || '',
      dateOfBirth: applicant.dateOfBirth || '',
      learnerLicenseNumber: applicant.learnerLicenseNumber || '',
      drivingLicenseNumber: applicant.drivingLicenseNumber || '',
    });
    setError('');
    setMessage('');
  };

  const remove = async (id) => {
    setError('');
    setMessage('');
    try {
      await deleteApplicant(id);
      setMessage('Applicant record deleted successfully.');
      load();
      if (editingId === id) reset();
    } catch (err) {
      setError(validationMessage(err));
    }
  };

  return (
    <AppLayout title="Applicant Management" subtitle="US-014 / US-017 - Manage and validate applicant records">
      <div className="page-header">
        <h1>Applicant Records</h1>
        <p>Create, update, and maintain applicant information.</p>
      </div>

      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card" style={{ marginBottom: 24 }}>
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              <div className="form-group">
                <label>Full Name</label>
                <input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Phone</label>
                <input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Aadhaar Number</label>
                <input value={form.aadhaarNumber} onChange={(e) => setForm({ ...form, aadhaarNumber: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Date of Birth</label>
                <input type="date" value={form.dateOfBirth} onChange={(e) => setForm({ ...form, dateOfBirth: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Address</label>
                <input value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Learner License Number</label>
                <input value={form.learnerLicenseNumber} onChange={(e) => setForm({ ...form, learnerLicenseNumber: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Driving License Number</label>
                <input value={form.drivingLicenseNumber} onChange={(e) => setForm({ ...form, drivingLicenseNumber: e.target.value })} />
              </div>
            </div>
            <div style={{ display: 'flex', gap: 10 }}>
              <button className="btn btn-blue" type="submit" disabled={saving}>
                {saving ? 'Saving...' : editingId ? 'Update Applicant' : 'Create Applicant'}
              </button>
              {editingId && <button className="btn btn-outline" type="button" onClick={reset}>Cancel</button>}
            </div>
          </form>
        </div>
      </div>

      {loading ? (
        <div className="empty-state">Loading applicants...</div>
      ) : applicants.length === 0 ? (
        <div className="empty-state">No applicant records found.</div>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Aadhaar</th>
                <th>DL Number</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {applicants.map((a) => (
                <tr key={a.applicantId}>
                  <td>{a.applicantId}</td>
                  <td>{a.fullName}</td>
                  <td>{a.email}</td>
                  <td>{a.phone}</td>
                  <td>{a.aadhaarNumber}</td>
                  <td>{a.drivingLicenseNumber || '-'}</td>
                  <td>
                    <button className="btn btn-outline" style={{ padding: '6px 10px', marginRight: 6 }} onClick={() => edit(a)}>Edit</button>
                    <button className="btn btn-danger" style={{ padding: '6px 10px' }} onClick={() => remove(a.applicantId)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </AppLayout>
  );
}
