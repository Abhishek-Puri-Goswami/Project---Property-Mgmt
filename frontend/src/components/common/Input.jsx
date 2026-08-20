export default function Input({ label, name, type = 'text', value, onChange, error, placeholder }) {
  return (
    <div style={{ marginBottom: 'var(--space-md)' }}>
      {label && (
        <label htmlFor={name} style={{ display: 'block', marginBottom: 'var(--space-xs)', fontWeight: 600 }}>
          {label}
        </label>
      )}
      <input
        id={name}
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className="clay-inset"
        style={{
          width: '100%',
          padding: '10px 14px',
          fontSize: '1rem',
          color: 'var(--color-text)',
        }}
      />
      {error && (
        <span style={{ color: 'var(--color-error)', fontSize: '0.85rem' }}>{error}</span>
      )}
    </div>
  )
}
