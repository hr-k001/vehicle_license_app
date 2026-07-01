import { useState } from 'react';
import AppLayout from '../../components/AppLayout';
import { searchApplications } from '../../api/api';

const badgeClass = { PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected' };

export default function SearchApplications() {
  const [query, setQuery]   = useState('');
  const [results, setResults] = useState([]);
  const [searched, setSearched] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError]   = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    setLoading(true); setError(''); setResults([]); setSearched(false);
    try {
      const res = await searchApplications(query.trim());
      setResults(res.data);
      setSearched(true);
    } catch {
      setError('Search failed. Please try again.');
    } finally { setLoading(false); }
  };

  return (
    <AppLayout title="Search Applications" subtitle="US-013 · Find applications by name or number">
      <div className="page-header">
        <h1>Search Applications</h1>
        <p>Search across all applications by application number, applicant email, or name.</p>
      </div>

      {error && <div className="alert alert-danger">⚠ {error}</div>}

      <div className="card">
        <div className="card-body">
          <form onSubmit={handleSearch}>
            <div style={{ display: 'flex', gap: 12 }}>
              <div className="form-group" style={{ flex: 1, margin: 0 }}>
                <input placeholder="Search by name, email, or app number (e.g. LL-1001, Kumar, test@email.com)"
                  value={query} onChange={e => { setQuery(e.target.value); setSearched(false); }} />
              </div>
              <button type="submit" className="btn btn-primary" disabled={loading} style={{ whiteSpace: 'nowrap' }}>
                {loading ? <><span className="spinner" /> Searching...</> : '🔍 Search'}
              </button>
            </div>
          </form>
        </div>
      </div>

      {searched && results.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">🔍</div>
          <div className="empty-title">No Results Found</div>
          <div className="empty-sub">No applications matched "{query}". Try a different keyword.</div>
        </div>
      )}

      {results.length > 0 && (
        <>
          <div style={{ fontSize: '0.82rem', color: '#6b7280', marginBottom: 8 }}>
            Found <strong>{results.length}</strong> result{results.length !== 1 ? 's' : ''} for "{query}"
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Application No.</th>
                  <th>Type</th>
                  <th>Applicant Name</th>
                  <th>Email</th>
                  <th>Mobile</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {results.map(a => {
                  const name = [a.applicant?.firstName, a.applicant?.middleName, a.applicant?.lastName].filter(Boolean).join(' ');
                  const type = a.type ?? a.applicationType ?? '—';
                  return (
                    <tr key={a.applicationNumber}>
                      <td><code style={{ fontSize: '0.82rem' }}>{a.applicationNumber}</code></td>
                      <td><span className={`badge ${type === 'DL' ? 'badge-info' : 'badge-pending'}`}>{type}</span></td>
                      <td>{name || '—'}</td>
                      <td style={{ fontSize: '0.82rem', color: '#6b7280' }}>{a.applicant?.email || '—'}</td>
                      <td style={{ fontSize: '0.82rem' }}>{a.applicant?.mobile || '—'}</td>
                      <td><span className={`badge ${badgeClass[a.status] ?? 'badge-info'}`}>{a.status}</span></td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </>
      )}
    </AppLayout>
  );
}
