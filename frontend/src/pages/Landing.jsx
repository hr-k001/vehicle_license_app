import { useNavigate } from 'react-router-dom';

const features = [
  { icon: '📋', bg: '#eff6ff', title: 'Apply for Licenses', desc: 'Submit Learner License and Driving License applications online in minutes.' },
  { icon: '🔍', bg: '#ecfdf5', title: 'Track Applications', desc: 'Check real-time status of your LL and DL applications anytime.' },
  { icon: '📅', bg: '#fff7ed', title: 'Schedule Tests', desc: 'Book your driving test slot at your preferred date from the portal.' },
  { icon: '🏛️', bg: '#fdf4ff', title: 'RTO Management', desc: 'Officers can approve, reject, and search applications from a dedicated panel.' },
  { icon: '🔒', bg: '#fef2f2', title: 'Secure & Reliable', desc: 'Role-based access ensures applicants and officers see only what they need.' },
  { icon: '⚡', bg: '#fefce8', title: 'Fast Processing', desc: 'Streamlined workflows reduce application turnaround time significantly.' },
];

export default function Landing() {
  const navigate = useNavigate();

  return (
    <div className="landing">
      {/* Navbar */}
      <nav className="landing-nav">
        <div className="landing-nav-logo">Vehicle<span>License</span> Portal</div>
        <div className="landing-nav-badge">Navikenz Training Project</div>
      </nav>

      {/* Hero */}
      <section className="landing-hero">
        <div className="hero-content">
          <div className="hero-tag">
            <span className="hero-tag-dot" />
            Online Vehicle License Management
          </div>

          <h1 className="hero-title">
            Apply. Track.<br />
            <span className="highlight">Get Licensed.</span>
          </h1>

          <p className="hero-sub">
            A seamless government portal for applying Learner &amp; Driving Licenses,
            scheduling tests, and RTO officers to manage applications — all in one place.
          </p>

          <div className="hero-cards">
            <div className="hero-card" onClick={() => navigate('/auth?role=applicant')}>
              <div className="hero-card-icon">👤</div>
              <h3>I'm an Applicant</h3>
              <p>Apply for Learner or Driving License, track your application status, and schedule your driving test.</p>
              <div className="hero-card-btn">Get Started →</div>
            </div>

            <div className="hero-card" onClick={() => navigate('/auth?role=rto')}>
              <div className="hero-card-icon">🏛️</div>
              <h3>I'm an RTO Officer</h3>
              <p>Review and manage license applications, approve or reject submissions, and search applicant records.</p>
              <div className="hero-card-btn">Officer Login →</div>
            </div>
          </div>

          <div className="hero-stats">
            <div className="hero-stat">
              <div className="hero-stat-val">LL + DL</div>
              <div className="hero-stat-label">License Types Supported</div>
            </div>
            <div className="hero-stat">
              <div className="hero-stat-val">100%</div>
              <div className="hero-stat-label">Online Process</div>
            </div>
            <div className="hero-stat">
              <div className="hero-stat-val">Real-time</div>
              <div className="hero-stat-label">Application Tracking</div>
            </div>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="landing-features">
        <h2 className="landing-features-title">Everything you need in one portal</h2>
        <p className="landing-features-sub">Complete license management from application to approval, fully digital.</p>
        <div className="features-grid">
          {features.map(f => (
            <div className="feature-card" key={f.title}>
              <div className="feature-card-icon" style={{ background: f.bg }}>{f.icon}</div>
              <h4>{f.title}</h4>
              <p>{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      <footer className="landing-footer">
        © 2026 Vehicle License Portal &nbsp;·&nbsp; Navikenz Training Project &nbsp;·&nbsp; Built with Spring Boot + React
      </footer>
    </div>
  );
}
