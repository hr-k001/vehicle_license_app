import { useState, useCallback } from 'react';

let _show = null;

export function useToast() {
  const show = useCallback((msg, type = 'success') => {
    if (_show) _show(msg, type);
  }, []);
  return { toast: show };
}

export function ToastContainer() {
  const [toasts, setToasts] = useState([]);

  _show = (msg, type) => {
    const id = Date.now();
    setToasts(p => [...p, { id, msg, type }]);
    setTimeout(() => setToasts(p => p.filter(t => t.id !== id)), 3500);
  };

  return (
    <div className="toast-container">
      {toasts.map(t => (
        <div key={t.id} className={`toast ${t.type}`}>
          <span>{t.type === 'success' ? '✓' : '✕'}</span>
          <span>{t.msg}</span>
        </div>
      ))}
    </div>
  );
}
