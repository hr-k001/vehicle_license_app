import { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { registerUser, loginUser } from '../api/api';

const applicantFeatures = [
  'Apply for Learner License online',
  'Track your application status',
  'Schedule your driving test',
  'Get DL after approval',
];
const rtoFeatures = [
  'View all submitted applications',
  'Approve or reject LL & DL',
  'Search applicant records',
  'Manage application workflow',
];

export default function Auth() {
  const [params] = useSearchParams();
  const role = params.get('role') || 'applicant';
  const [tab, setTab] = useState('login');
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const isRTO = role === 'rto';
  const roleLabel = isRTO ? 'RTO Officer' : 'Applicant';
  const dashPath = isRTO ? '/app/rto-dashboard' : '/app/dashboard';
  const features = isRTO ? rtoFeatures : applicantFeatures;

  const set = (k, v) => { setForm(f => ({ ...f, [k]: v })); setError(''); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.email || !form.password) { setError('Please fill in all fields.'); return; }
    setLoading(true); setError('');
    try {
      if (tab === 'register') {
        const res = await registerUser(form);
        if (res.data.message === 'User already exists') {
          setError('This email is already registered. Please sign in.');
          return;
        }
        login(form.email, role);
        navigate(dashPath);
      } else {
        const res = await loginUser(form);
        if (res.data.message === 'Login successful') {
          login(form.email, role);
          navigate(dashPath);
        } else {
          setError('Invalid email or password.');
        }
      }
    } catch (err) {
      if (err.response?.status === 401) setError('Invalid email or password.');
      else setError('Server error. Make sure the backend is running on port 8080.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      {/* Left decorative panel */}
      <div className="auth-left">
        <div className="auth-left-content">
          <div className="auth-left-icon">{isRTO ? '🏛️' : '📄'}</div>
          <h2>{isRTO ? 'RTO Officer Portal' : 'Applicant Portal'}</h2>
          <p>
            {isRTO
              ? 'Manage all license applications, approve or reject submissions, and search applicant records from one place.'
              : 'Apply for your Learner or Driving License and track every step of your application in real time.'}
          </p>
          <div className="auth-features">
            {features.map(f => (
              <div className="auth-feature-item" key={f}>
                <div className="auth-feature-check">✓</div>
                {f}
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Right form panel */}
      <div className="auth-right">
        <div className="auth-back" onClick={() => navigate('/')}>
          ← Back to home
        </div>

        <div className="auth-role-badge">
          {isRTO ? '🏛️' : '👤'}&nbsp;&nbsp;{roleLabel}
        </div>
        <h2>{tab === 'login' ? 'Welcome back' : 'Create account'}</h2>
        <p className="auth-sub">
          {tab === 'login'
            ? `Sign in to your ${roleLabel.toLowerCase()} account to continue.`
            : `Register a new account to start your license journey.`}
        </p>

        <div className="auth-tabs">
          <div
            className={`auth-tab ${tab === 'login' ? 'active' : ''}`}
            onClick={() => { setTab('login'); setError(''); }}
          >
            Sign In
          </div>
          <div
            className={`auth-tab ${tab === 'register' ? 'active' : ''}`}
            onClick={() => { setTab('register'); setError(''); }}
          >
            Register
          </div>
        </div>

        {error && <div className="alert alert-danger">⚠ {error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email Address</label>
            <input
              type="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={e => set('email', e.target.value)}
              autoFocus
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              placeholder="••••••••"
              value={form.password}
              onChange={e => set('password', e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-primary btn-full btn-lg" disabled={loading}>
            {loading
              ? <><span className="spinner" /> Processing...</>
              : tab === 'login' ? 'Sign In' : 'Create Account'}
          </button>
        </form>

        <div style={{ marginTop: 20, textAlign: 'center', fontSize: '0.82rem', color: 'var(--text-400)' }}>
          {tab === 'login' ? "Don't have an account? " : 'Already have an account? '}
          <span
            style={{ color: 'var(--secondary)', cursor: 'pointer', fontWeight: 600 }}
            onClick={() => { setTab(tab === 'login' ? 'register' : 'login'); setError(''); }}
          >
            {tab === 'login' ? 'Register' : 'Sign In'}
          </span>
        </div>
      </div>
    </div>
  );
}
