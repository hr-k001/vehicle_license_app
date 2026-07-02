import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { generateLicenseNumber } from '../../api/api';

export default function LicenseGeneration() {
  const [applicationNumber, setApplicationNumber] = useState('');
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleGenerate = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setResult(null);

    try {
      const res = await generateLicenseNumber(applicationNumber.trim());
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Could not generate license number.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AppLayout title="Generate License" subtitle="US-015 - Generate unique license number">
      <div className="page-header">
        <h1>License Number Generation</h1>
        <p>Generate a unique license number for an approved DL application.</p>
      </div>

      <div className="card">
        <div className="card-body">
          <form onSubmit={handleGenerate}>
            <div className="form-group">
              <label>DL Application Number</label>
              <input
                placeholder="e.g. DL-2001"
                value={applicationNumber}
                onChange={(e) => {
                  setApplicationNumber(e.target.value);
                  setError('');
                  setResult(null);
                }}
                required
              />
            </div>
            {error && <div className="alert alert-danger">{error}</div>}
            <button className="btn btn-blue" disabled={loading}>
              {loading ? 'Generating...' : 'Generate License Number'}
            </button>
          </form>

          {result && (
            <div className="result-box" style={{ marginTop: 24 }}>
              <h4>Generated License</h4>
              <div className="detail-row"><span>Message</span><strong>{result.message}</strong></div>
              <div className="detail-row"><span>License Number</span><strong>{result.licenseNumber}</strong></div>
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
