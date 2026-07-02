import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { getLicenseDetails } from '../../api/api';

const fields = [
  { key: 'applicantName', label: 'Applicant Name' },
  { key: 'licenseNumber', label: 'License Number' },
  { key: 'issueDate', label: 'Issue Date', type: 'date' },
  { key: 'expiryDate', label: 'Expiry Date', type: 'date' },
  { key: 'vehicleCategory', label: 'Vehicle Category' },
  { key: 'status', label: 'Status', type: 'status' },
];

function formatDate(value) {
  if (!value) return '-';
  return new Date(value).toLocaleDateString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
}

function formatValue(field, details) {
  const value = details[field.key];
  if (field.type === 'date') return formatDate(value);
  return value || '-';
}

export default function ViewLicenseDetails() {
  const [licenseNumber, setLicenseNumber] = useState('');
  const [details, setDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    const trimmed = licenseNumber.trim();
    if (!trimmed) return;

    setLoading(true);
    setError('');
    setDetails(null);

    try {
      const res = await getLicenseDetails(trimmed);
      setDetails(res.data);
    } catch (err) {
      if (err.response?.status === 404) {
        setError('No issued license found with that license number.');
      } else {
        setError('Could not fetch license details. Ensure the backend is running.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <AppLayout title="License Details" subtitle="US-016 - View issued license">
      <div className="page-header">
        <h1>View Issued License</h1>
        <p>Enter your license number to view the issued license details.</p>
      </div>

      <div className="card">
        <div className="card-body">
          <form onSubmit={handleSearch}>
            <div className="form-group">
              <label>License Number</label>
              <input
                placeholder="e.g. DL-20260701-1234"
                value={licenseNumber}
                onChange={(e) => {
                  setLicenseNumber(e.target.value);
                  setError('');
                  setDetails(null);
                }}
                required
              />
            </div>

            {error && <div className="alert alert-danger">! {error}</div>}

            <button type="submit" className="btn btn-blue" disabled={loading}>
              {loading ? <><span className="spinner" /> Fetching...</> : 'View Details'}
            </button>
          </form>

          {details && (
            <div className="result-box" style={{ marginTop: 24 }}>
              <h4>Issued License</h4>
              <div className="detail-grid">
                {fields.map((field) => (
                  <div className="detail-row" key={field.key}>
                    <span>{field.label}</span>
                    {field.type === 'status' ? (
                      <strong>
                        <span className="badge badge-approved">{formatValue(field, details)}</span>
                      </strong>
                    ) : (
                      <strong>{formatValue(field, details)}</strong>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
