export default function Card({ children, style }) {
  return (
    <div className="clay" style={{ padding: 'var(--space-lg)', ...style }}>
      {children}
    </div>
  )
}
