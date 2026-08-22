import { describe, it, expect, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { AuthProvider, useAuth, STORAGE_KEY } from './AuthContext.jsx'

function Probe() {
  const { user, isAuthenticated, login, logout } = useAuth()
  return (
    <div>
      <span data-testid="status">{isAuthenticated ? 'authed' : 'anon'}</span>
      <span data-testid="user">{user ? user.email : 'none'}</span>
      <span data-testid="error"></span>
      <button onClick={() => login({ email: 'admin@example.com', role: 'ADMIN' }, 'jwt-token')}>login-admin</button>
      <button
        onClick={() => {
          try {
            login({ email: 'buyer@example.com', role: 'BUYER' }, 'jwt-token')
          } catch (error) {
            document.querySelector('[data-testid="error"]').textContent = error.message
          }
        }}
      >
        login-buyer
      </button>
      <button onClick={logout}>logout</button>
    </div>
  )
}

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('login succeeds for an ADMIN user and persists to localStorage', () => {
    render(
      <AuthProvider>
        <Probe />
      </AuthProvider>
    )

    fireEvent.click(screen.getByText('login-admin'))

    expect(screen.getByTestId('status').textContent).toBe('authed')
    expect(JSON.parse(localStorage.getItem(STORAGE_KEY)).user.role).toBe('ADMIN')
  })

  it('login rejects a non-ADMIN user and does not persist', () => {
    render(
      <AuthProvider>
        <Probe />
      </AuthProvider>
    )

    fireEvent.click(screen.getByText('login-buyer'))

    expect(screen.getByTestId('status').textContent).toBe('anon')
    expect(screen.getByTestId('error').textContent).toBe('Only administrators can access this panel')
    expect(localStorage.getItem(STORAGE_KEY)).toBeNull()
  })

  it('logout clears auth state', () => {
    render(
      <AuthProvider>
        <Probe />
      </AuthProvider>
    )

    fireEvent.click(screen.getByText('login-admin'))
    fireEvent.click(screen.getByText('logout'))

    expect(screen.getByTestId('status').textContent).toBe('anon')
    expect(localStorage.getItem(STORAGE_KEY)).toBeNull()
  })
})
