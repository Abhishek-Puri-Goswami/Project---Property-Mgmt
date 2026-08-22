export default function Select({ label, name, value, onChange, options, error }) {
  return (
    <div style={{ marginBottom: 'var(--space-md)' }}>
      {label && (
        <label htmlFor={name} style={{ display: 'block', marginBottom: 'var(--space-xs)', fontWeight: 600 }}>
          {label}
        </label>
      )}
      <select
        id={name}
        name={name}
        value={value}
        onChange={onChange}
        className="clay-inset"
        style={{ width: '100%', padding: '10px 14px', fontSize: '1rem', color: 'var(--color-text)' }}
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      {error && (
        <span style={{ color: 'var(--color-error)', fontSize: '0.85rem' }}>{error}</span>
      )}
    </div>
  )
}
