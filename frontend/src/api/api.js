import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

const api = axios.create({ baseURL: BASE_URL });

// US-001
export const registerUser = (data) => api.post('/users/register', data);
// US-002
export const loginUser = (data) => api.post('/users/login', data);
// US-003
export const applyLL = (data) => api.post('/license/ll/apply', data);
// US-004
export const getLLStatus = (appNo) => api.get(`/license/ll/status/${appNo}`);
// US-005
export const approveLL = (appNo) => api.put(`/rto/ll/approve/${appNo}`);
// US-006
export const rejectLL = (appNo) => api.put(`/rto/ll/reject/${appNo}`);
// US-007
export const applyDL = (data) => api.post('/license/dl/apply', data);
// US-008
export const scheduleTest = (appNo, data) => api.put(`/license/dl/${appNo}/schedule-test`, data);
// US-009
export const getDLStatus = (appNo) => api.get(`/license/dl/status/${appNo}`);
// US-010
export const approveDL = (appNo) => api.put(`/rto/dl/approve/${appNo}`);
// US-011
export const rejectDL = (appNo) => api.put(`/rto/dl/reject/${appNo}`);
// US-012
export const getAllApplications = () => api.get('/rto/applications');
// US-013
export const searchApplications = (query) => api.get(`/rto/applications/search?q=${query}`);

// Check if applicant has approved LL (before DL application)
export const checkLLByEmail = (email) => api.get(`/license/ll/check-by-email?email=${encodeURIComponent(email)}`);

// RTO: mark driving test result
export const passTest  = (appNo) => api.put(`/rto/test/pass/${appNo}`);
export const failTest  = (appNo) => api.put(`/rto/test/fail/${appNo}`);
