import { Link } from 'react-router-dom';
import AppLayout from '../../components/AppLayout';
import { useAuth } from '../../context/AuthContext';

const actions = [
  {
    to: '/app/apply-ll',
    icon: '📋',
    bg: '#eff6ff',
    label: 'Apply for LL',
    desc: 'Submit learner license application',
  },
  {
    to: '/app/ll-status',
    icon: '🔍',
    bg: '#ecfdf5',
    label: 'Track LL Status',
    desc: 'Check your LL application status',
  },
  {
    to: '/app/apply-dl',
    icon: '🚗',
    bg: '#fdf4ff',
    label: 'Apply for DL',
    desc: 'Submit driving license application',
  },
  {
    to: '/app/schedule-test',
    icon: '📅',
    bg: '#fff7ed',
    label: 'Schedule Test',
    desc: 'Book your driving test slot',
  },
  {
    to: '/app/dl-status',
    icon: '🔎',
    bg: '#fefce8',
    label: 'Track DL Status',
    desc: 'Check your DL application status',
  },
  {
    to: '/app/license-details',
    icon: '🪪',
    bg: '#eef2ff',
    label: 'View License Details',
    desc: 'View issued license information',
  },
];

const journeySteps = ['Register', 'Apply LL', 'LL Approval', 'Apply DL', 'Book Test', 'DL Issued'];

export default function ApplicantDashboard() {
  const { user } = useAuth();
  const name = user?.email?.split('@')[0] || 'Applicant';
  const displayName = name.charAt(0).toUpperCase() + name.slice(1);

  return (
    <AppLayout title="Dashboard" subtitle="Your license application overview">

      {/* Welcome banner */}
      <div className="welcome-banner">
        <div className="welcome-text">
          <div className="welcome-greeting">👋 Welcome back, {displayName}!</div>
          <div className="welcome-desc">
            Manage your Learner and Driving License applications from one place.
            Start by applying for a Learner License if you haven't already.
          </div>
        </div>
        <div className="welcome-icon">🪪</div>
      </div>

      {/* Section heading */}
      <div className="page-header">
        <h1>Quick Actions</h1>
        <p>Select an action to get started with your license journey.</p>
      </div>

      {/* Action cards */}
      <div className="actions-grid">
        {actions.map(a => (
          <Link key={a.to} to={a.to} className="action-card">
            <div className="a-icon" style={{ background: a.bg }}>{a.icon}</div>
            <div className="a-body">
              <div className="a-title">{a.label}</div>
              <div className="a-desc">{a.desc}</div>
            </div>
            <div className="a-arrow">›</div>
          </Link>
        ))}
      </div>

      {/* License journey stepper */}
      <div className="journey-card">
        <div className="journey-title">License Journey</div>
        <div className="journey-sub">Typical steps to obtain your Driving License</div>
        <div className="journey-steps">
          {journeySteps.map((step, i) => (
            <div className="journey-step" key={step}>
              <div className="journey-step-inner">
                <div className="journey-step-circle">{i + 1}</div>
                <div className="journey-step-label">{step}</div>
              </div>
              {i < journeySteps.length - 1 && <div className="journey-connector" />}
            </div>
          ))}
        </div>
      </div>
    </AppLayout>
  );
}
