import { useToast } from '../../context/ToastContext.jsx'

const typeColors = {
  success: 'var(--color-success)',
  error: 'var(--color-error)',
  info: 'var(--color-info)',
}

export default function Toast() {
  const { toasts, dismissToast } = useToast()

  if (toasts.length === 0) {
    return null
  }

  return (
    <div style={{ position: 'fixed', bottom: 'var(--space-lg)', right: 'var(--space-lg)', zIndex: 2000 }}>
      {toasts.map((toast) => (
        <div
          key={toast.id}
          role="status"
          className="clay"
          onClick={() => dismissToast(toast.id)}
          style={{
            padding: '12px 20px',
            marginTop: 'var(--space-sm)',
            borderLeft: `4px solid ${typeColors[toast.type] || typeColors.info}`,
            cursor: 'pointer',
          }}
        >
          {toast.message}
        </div>
      ))}
    </div>
  )
}
