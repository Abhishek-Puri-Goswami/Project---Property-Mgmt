export default function Modal({ open, title, children, onClose }) {
  if (!open) {
    return null
  }

  return (
    <div
      role="dialog"
      aria-modal="true"
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(0,0,0,0.3)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000,
      }}
    >
      <div className="clay" style={{ padding: 'var(--space-lg)', minWidth: '320px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 'var(--space-md)' }}>
          <h2 style={{ margin: 0 }}>{title}</h2>
          <button
            aria-label="Close"
            onClick={onClose}
            style={{ background: 'none', border: 'none', fontSize: '1.2rem', cursor: 'pointer' }}
          >
            ×
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
