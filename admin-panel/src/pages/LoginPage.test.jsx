import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext.jsx'
import { ToastProvider } from '../context/ToastContext.jsx'
import * as adminApi from '../api/adminApi.js'
import LoginPage from './LoginPage.jsx'

vi.mock('../api/adminApi.js')

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

  it('logs in successfully for an ADMIN user', async () => {
    adminApi.login.mockResolvedValue({
      token: 'jwt-token',
      user: { id: 1, email: 'admin@example.com', role: 'ADMIN' },
    })
    renderPage()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'admin@example.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'secret123' } })
    fireEvent.click(screen.getByRole('button', { name: 'Login' }))

    await waitFor(() => expect(adminApi.login).toHaveBeenCalledOnce())
  })

  it('shows an error message when a non-admin logs in', async () => {
    adminApi.login.mockResolvedValue({
      token: 'jwt-token',
      user: { id: 2, email: 'buyer@example.com', role: 'BUYER' },
    })
    renderPage()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'buyer@example.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'secret123' } })
    fireEvent.click(screen.getByRole('button', { name: 'Login' }))

    expect(await screen.findByText('Only administrators can access this panel')).toBeInTheDocument()
  })

  it('shows an error message on failed login', async () => {
    adminApi.login.mockRejectedValue({ message: 'Invalid email or password' })
    renderPage()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'admin@example.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'wrong' } })
    fireEvent.click(screen.getByRole('button', { name: 'Login' }))

    expect(await screen.findByText('Invalid email or password')).toBeInTheDocument()
  })
})
