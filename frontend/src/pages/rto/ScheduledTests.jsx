import { useState, useEffect, useCallback } from 'react';
import AppLayout from '../../components/AppLayout';
import { getAllApplications, passTest, failTest } from '../../api/api';

function testResultBadge(result) {
  if (!result) return <span className="badge badge-yellow">PENDING</span>;
  return <span className={`badge ${result === 'PASS' ? 'badge-green' : 'badge-red'}`}>{result}</span>;
}

export default function ScheduledTests() {
  const [apps, setApps]       = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter]   = useState('PENDING'); // PENDING | ALL
  const [msgMap, setMsgMap]   = useState({}); // appNo → {msg, err}
  const [acting, setActing]   = useState(null); // appNo currently acting

  const load = useCallback(() => {
    setLoading(true);
    getAllApplications()
      .then(r => {
        const scheduled = (r.data || []).filter(a => a.type === 'DL' && a.testDate);
        setApps(scheduled);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  const doResult = async (fn, appNo, label) => {
    setActing(appNo);
    setMsgMap(m => ({ ...m, [appNo]: { msg: '', err: '' } }));
    try {
      await fn(appNo);
      setMsgMap(m => ({ ...m, [appNo]: { msg: `Marked as ${label}.`, err: '' } }));
      load();
    } catch (e) {
      setMsgMap(m => ({ ...m, [appNo]: { msg: '', err: e.response?.data?.message || 'Action failed.' } }));
    } finally { setActing(null); }
  };

  const displayed = filter === 'PENDING'
    ? apps.filter(a => !a.testResult)
    : apps;

  return (
    <AppLayout title="Scheduled Driving Tests" subtitle="Mark test results for scheduled DL applicants">
      <div className="page-header">
        <h1>Scheduled Driving Tests</h1>
        <p>Mark each applicant's driving test as PASS or FAIL. A PASS result is required before approving the DL.</p>
      </div>

      <div className="list-controls" style={{ marginBottom: 16 }}>
        <div className="filter-tabs">
          <button className={`filter-tab${filter === 'PENDING' ? ' active' : ''}`} onClick={() => setFilter('PENDING')}>
            Awaiting Result
            <span className="tab-count">{apps.filter(a => !a.testResult).length}</span>
          </button>
          <button className={`filter-tab${filter === 'ALL' ? ' active' : ''}`} onClick={() => setFilter('ALL')}>
            All Scheduled
            <span className="tab-count">{apps.length}</span>
          </button>
        </div>
      </div>

      {loading ? (
        <div className="empty-state">Loading…</div>
      ) : displayed.length === 0 ? (
        <div className="empty-state">
          {filter === 'PENDING' ? 'No pending tests — all scheduled tests have results.' : 'No scheduled tests found.'}
        </div>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>App No.</th>
                <th>Applicant</th>
                <th>Email</th>
                <th>Vehicle Type</th>
                <th>Test Date &amp; Time</th>
                <th>Result</th>
                <th style={{ textAlign: 'center' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {displayed.map(a => {
                const fb = msgMap[a.applicationNumber] || {};
                const isActing = acting === a.applicationNumber;
                return (
                  <>
                    <tr key={a.applicationNumber} className={a.testResult ? '' : 'row-highlight'}>
                      <td><code>{a.applicationNumber}</code></td>
                      <td>{a.applicant?.firstName} {a.applicant?.lastName}</td>
                      <td>{a.applicant?.email}</td>
                      <td>{a.applicant?.vehicleType || '—'}</td>
                      <td>{a.testDate ? new Date(a.testDate).toLocaleString('en-IN') : '—'}</td>
                      <td>{testResultBadge(a.testResult)}</td>
                      <td>
                        {!a.testResult ? (
                          <div className="action-btns">
                            <button
                              className="btn btn-success btn-sm"
                              disabled={isActing}
                              onClick={() => doResult(passTest, a.applicationNumber, 'PASS')}
                            >
                              {isActing ? '…' : '✅ PASS'}
                            </button>
                            <button
                              className="btn btn-danger btn-sm"
                              disabled={isActing}
                              onClick={() => doResult(failTest, a.applicationNumber, 'FAIL')}
                            >
                              {isActing ? '…' : '❌ FAIL'}
                            </button>
                          </div>
                        ) : (
                          <span className="result-locked">
                            {a.testResult === 'PASS' ? '✅ Passed' : '❌ Failed'}
                          </span>
                        )}
                      </td>
                    </tr>
                    {(fb.msg || fb.err) && (
                      <tr key={`${a.applicationNumber}-fb`} className="feedback-row">
                        <td colSpan={7}>
                          {fb.msg && <span className="inline-success">✓ {fb.msg}</span>}
                          {fb.err && <span className="inline-error">⚠ {fb.err}</span>}
                        </td>
                      </tr>
                    )}
                  </>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </AppLayout>
  );
}
