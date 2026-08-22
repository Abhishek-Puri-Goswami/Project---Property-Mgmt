import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import * as adminApi from '../api/adminApi.js'
import SystemStatusPage from './SystemStatusPage.jsx'

vi.mock('../api/adminApi.js')

describe('SystemStatusPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders service instances with their status', async () => {
    adminApi.listServiceInstances.mockResolvedValue([
      { id: '1', registration: { name: 'auth-service', serviceUrl: 'http://localhost:8081' }, statusInfo: { status: 'UP' } },
    ])

    render(<SystemStatusPage />)

    expect(await screen.findByText('auth-service')).toBeInTheDocument()
    expect(screen.getByText('UP')).toBeInTheDocument()
  })

  it('shows an empty state when there are no instances', async () => {
    adminApi.listServiceInstances.mockResolvedValue([])

    render(<SystemStatusPage />)

    expect(await screen.findByText('No registered services found.')).toBeInTheDocument()
  })

  it('shows an error state when the call fails', async () => {
    adminApi.listServiceInstances.mockRejectedValue(new Error('failed'))

    render(<SystemStatusPage />)

    expect(await screen.findByText('Unable to load service status.')).toBeInTheDocument()
  })
})
