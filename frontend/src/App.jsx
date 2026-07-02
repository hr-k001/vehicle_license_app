import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastContainer } from './components/Toast';

import Landing   from './pages/Landing';
import Auth      from './pages/Auth';

import ApplicantDashboard from './pages/applicant/ApplicantDashboard';
import ApplyLL            from './pages/applicant/ApplyLL';
import ViewLLStatus       from './pages/applicant/ViewLLStatus';
import ApplyDL            from './pages/applicant/ApplyDL';
import ScheduleTest       from './pages/applicant/ScheduleTest';
import ViewDLStatus       from './pages/applicant/ViewDLStatus';
import ViewLicenseDetails from './pages/applicant/ViewLicenseDetails';

import RTODashboard       from './pages/rto/RTODashboard';
import LLApplications     from './pages/rto/LLApplications';
import DLApplications     from './pages/rto/DLApplications';
import ScheduledTests     from './pages/rto/ScheduledTests';
import ManageApplications from './pages/rto/ManageApplications';
import SearchApplications from './pages/rto/SearchApplications';
import ApplicantManagement from './pages/rto/ApplicantManagement';
import LicenseGeneration  from './pages/rto/LicenseGeneration';
import Reports            from './pages/rto/Reports';

import './App.css';

function RequireAuth({ children, role }) {
  const { user } = useAuth();
  const location = useLocation();
  if (!user) return <Navigate to="/auth" state={{ from: location }} replace />;
  if (role && user.role !== role) return <Navigate to="/" replace />;
  return children;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/"     element={<Landing />} />
      <Route path="/auth" element={<Auth />} />

      {/* Applicant routes */}
      <Route path="/app/dashboard"     element={<RequireAuth role="applicant"><ApplicantDashboard /></RequireAuth>} />
      <Route path="/app/apply-ll"      element={<RequireAuth role="applicant"><ApplyLL /></RequireAuth>} />
      <Route path="/app/ll-status"     element={<RequireAuth role="applicant"><ViewLLStatus /></RequireAuth>} />
      <Route path="/app/apply-dl"      element={<RequireAuth role="applicant"><ApplyDL /></RequireAuth>} />
      <Route path="/app/schedule-test" element={<RequireAuth role="applicant"><ScheduleTest /></RequireAuth>} />
      <Route path="/app/dl-status"     element={<RequireAuth role="applicant"><ViewDLStatus /></RequireAuth>} />
      <Route path="/app/license-details" element={<RequireAuth role="applicant"><ViewLicenseDetails /></RequireAuth>} />

      {/* RTO Officer routes */}
      <Route path="/app/rto-dashboard"         element={<RequireAuth role="rto"><RTODashboard /></RequireAuth>} />
      <Route path="/app/rto/ll-applications"   element={<RequireAuth role="rto"><LLApplications /></RequireAuth>} />
      <Route path="/app/rto/dl-applications"   element={<RequireAuth role="rto"><DLApplications /></RequireAuth>} />
      <Route path="/app/rto/scheduled-tests"   element={<RequireAuth role="rto"><ScheduledTests /></RequireAuth>} />
      <Route path="/app/rto/applications"      element={<RequireAuth role="rto"><ManageApplications /></RequireAuth>} />
      <Route path="/app/rto/search"            element={<RequireAuth role="rto"><SearchApplications /></RequireAuth>} />
      <Route path="/app/rto/applicants"        element={<RequireAuth role="rto"><ApplicantManagement /></RequireAuth>} />
      <Route path="/app/rto/generate-license"  element={<RequireAuth role="rto"><LicenseGeneration /></RequireAuth>} />
      <Route path="/app/rto/reports"           element={<RequireAuth role="rto"><Reports /></RequireAuth>} />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastContainer />
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}
