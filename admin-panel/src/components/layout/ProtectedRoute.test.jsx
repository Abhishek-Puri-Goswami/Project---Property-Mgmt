import { describe, it, expect, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider, STORAGE_KEY } from '../../context/AuthContext.jsx'
import ProtectedRoute from './ProtectedRoute.jsx'

function LoginStub() {
  return <div>Login Page</div>
}

function Home() {
  return <span>Protected Content</span>
}

function renderApp() {
  return render(
    <AuthProvider>
      <MemoryRouter initialEntries={['/']}>
        <Routes>
          <Route path="/login" element={<LoginStub />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Home />
              </ProtectedRoute>
            }
          />
        </Routes>
      </MemoryRouter>
    </AuthProvider>
  )
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('redirects to /login when unauthenticated', () => {
    renderApp()
    expect(screen.getByText('Login Page')).toBeInTheDocument()
  })

  it('renders the protected content when already authenticated', () => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({ user: { email: 'admin@example.com', role: 'ADMIN' }, token: 'jwt-token' }))
    renderApp()
    expect(screen.getByText('Protected Content')).toBeInTheDocument()
  })
})
