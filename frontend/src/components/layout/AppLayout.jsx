import { Link, Outlet } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext.jsx'
import Button from '../common/Button.jsx'

const NAV_LINKS = [
  { to: '/', label: 'Dashboard' },
  { to: '/properties', label: 'Search' },
  { to: '/favorites', label: 'Favorites' },
  { to: '/comparison', label: 'Compare' },
  { to: '/visits', label: 'Visits' },
  { to: '/ai-copilot', label: 'Ask AI' },
]

export default function AppLayout() {
  const { logout } = useAuth()

  return (
    <div>
      <nav
        className="clay"
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          margin: 'var(--space-md)',
          padding: 'var(--space-md) var(--space-lg)',
        }}
      >
        <strong>PropertyHub</strong>
        <div style={{ display: 'flex', gap: 'var(--space-md)' }}>
          {NAV_LINKS.map((link) => (
            <Link key={link.to} to={link.to} style={{ color: 'var(--color-text)' }}>
              {link.label}
            </Link>
          ))}
        </div>
        <Button variant="secondary" onClick={logout}>Logout</Button>
      </nav>
      <main style={{ margin: 'var(--space-md)' }}>
        <Outlet />
      </main>
    </div>
  )
}
