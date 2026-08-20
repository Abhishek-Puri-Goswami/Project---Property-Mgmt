import { describe, it, expect, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { AuthProvider, useAuth, STORAGE_KEY } from './AuthContext.jsx'

function Probe() {
  const { user, token, isAuthenticated, login, logout } = useAuth()
  return (
    <div>
      <span data-testid="status">{isAuthenticated ? 'authed' : 'anon'}</span>
      <span data-testid="user">{user ? user.email : 'none'}</span>
      <button onClick={() => login({ email: 'buyer@example.com' }, 'jwt-token')}>login</button>
      <button onClick={logout}>logout</button>
      <span data-testid="token">{token || 'none'}</span>
    </div>
  )
}

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('login sets user/token and persists to localStorage', () => {
    render(
      <AuthProvider>
        <Probe />
      </AuthProvider>
    )

    fireEvent.click(screen.getByText('login'))

    expect(screen.getByTestId('status').textContent).toBe('authed')
    expect(screen.getByTestId('user').textContent).toBe('buyer@example.com')
    expect(JSON.parse(localStorage.getItem(STORAGE_KEY)).token).toBe('jwt-token')
  })

  it('logout clears user/token and localStorage', () => {
    render(
      <AuthProvider>
        <Probe />
      </AuthProvider>
    )

    fireEvent.click(screen.getByText('login'))
    fireEvent.click(screen.getByText('logout'))

    expect(screen.getByTestId('status').textContent).toBe('anon')
    expect(localStorage.getItem(STORAGE_KEY)).toBeNull()
  })
})
