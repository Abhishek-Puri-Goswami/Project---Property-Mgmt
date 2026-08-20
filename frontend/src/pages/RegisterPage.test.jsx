import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { ToastProvider } from '../context/ToastContext.jsx'
import * as authApi from '../api/authApi.js'
import RegisterPage from './RegisterPage.jsx'

vi.mock('../api/authApi.js')

function renderPage() {
  return render(
    <ToastProvider>
      <MemoryRouter>
        <RegisterPage />
      </MemoryRouter>
    </ToastProvider>
  )
}

describe('RegisterPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('shows validation errors when submitted empty', () => {
    renderPage()
    fireEvent.click(screen.getByRole('button', { name: 'Register' }))
    expect(screen.getByText('Email is required')).toBeInTheDocument()
    expect(screen.getByText('Password is required')).toBeInTheDocument()
  })

  it('registers successfully', async () => {
    authApi.register.mockResolvedValue({ id: 1, email: 'buyer@example.com', role: 'BUYER' })
    renderPage()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'buyer@example.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'secret123' } })
    fireEvent.click(screen.getByRole('button', { name: 'Register' }))

    await waitFor(() => expect(authApi.register).toHaveBeenCalledOnce())
  })

  it('shows an error message on failed registration', async () => {
    authApi.register.mockRejectedValue({ message: 'Email already registered' })
    renderPage()

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'buyer@example.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'secret123' } })
    fireEvent.click(screen.getByRole('button', { name: 'Register' }))

    expect(await screen.findByText('Email already registered')).toBeInTheDocument()
  })
})
