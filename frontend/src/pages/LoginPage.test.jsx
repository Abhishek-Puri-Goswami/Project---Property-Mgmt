import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext.jsx'
import { ToastProvider } from '../context/ToastContext.jsx'
import * as authApi from '../api/authApi.js'
import LoginPage from './LoginPage.jsx'

vi.mock('../api/authApi.js')

function renderPage() {
  return render(
    <AuthProvider>
      <ToastProvider>
        <MemoryRouter>
          <LoginPage />
        </MemoryRouter>
      </ToastProvider>
    </AuthProvider>
  )
}

describe('LoginPage', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('shows validation errors when submitted empty', () => {
    renderPage()
    fireEvent.click(screen.getByRole('button', { name: 'Login' }))
    expect(screen.getByText('Email is required')).toBeInTheDocument()
    expect(screen.getByText('Password is required')).toBeInTheDocument()
  })

  it('logs in successfully and stores auth state', async () => {
    authApi.login.mockResolvedValue({
      token: 'jwt-token',
      user: { id: 1, email: 'buyer@example.com', role: 'BUYER' },
    })
    renderPage()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'buyer@example.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'secret123' } })
    fireEvent.click(screen.getByRole('button', { name: 'Login' }))

    await waitFor(() => expect(authApi.login).toHaveBeenCalledOnce())
  })

  it('shows an error message on failed login', async () => {
    authApi.login.mockRejectedValue({ message: 'Invalid email or password' })
    renderPage()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'buyer@example.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'wrong' } })
    fireEvent.click(screen.getByRole('button', { name: 'Login' }))

    expect(await screen.findByText('Invalid email or password')).toBeInTheDocument()
  })
})
