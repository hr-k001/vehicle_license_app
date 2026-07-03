const STORAGE_KEY = 'vlp_journey_progress';

const defaultProgress = {
  register: false,
  applyLL: false,
  llApproval: false,
  applyDL: false,
  bookTest: false,
  dlIssued: false,
};

function loadStore() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || {};
  } catch {
    return {};
  }
}

function saveStore(store) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(store));
}

export function getJourneyProgress(email) {
  if (!email) return defaultProgress;
  const store = loadStore();
  return { ...defaultProgress, ...(store[email] || {}) };
}

export function updateJourneyProgress(email, updates) {
  if (!email) return;
  const store = loadStore();
  const current = { ...defaultProgress, ...(store[email] || {}) };
  const next = { ...current, ...updates };
  store[email] = next;
  saveStore(store);
  return next;
}

export function resetJourneyProgress(email) {
  if (!email) return;
  const store = loadStore();
  delete store[email];
  saveStore(store);
}
