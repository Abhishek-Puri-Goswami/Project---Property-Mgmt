export default function Button({ children, onClick, type = 'button', variant = 'primary', disabled = false }) {
  const background = variant === 'primary' ? 'var(--color-primary)' : 'var(--color-surface)'
  const color = variant === 'primary' ? '#ffffff' : 'var(--color-text)'

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className="clay"
      style={{
        background,
        color,
        border: 'none',
        padding: '10px 20px',
        borderRadius: 'var(--radius-sm)',
        cursor: disabled ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.6 : 1,
        fontWeight: 600,
      }}
    >
      {children}
    </button>
  )
}
