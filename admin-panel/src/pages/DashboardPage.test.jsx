import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import * as adminApi from '../api/adminApi.js'
import DashboardPage from './DashboardPage.jsx'

vi.mock('../api/adminApi.js')

describe('DashboardPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders stat tiles from combined API responses', async () => {
    adminApi.listUsers.mockResolvedValue([
      { id: 1, email: 'buyer@example.com', role: 'BUYER', createdAt: '2026-08-21T00:00:00' },
      { id: 2, email: 'agent@example.com', role: 'AGENT', createdAt: '2026-08-21T00:00:00' },
    ])
    adminApi.listPropertiesAdmin.mockResolvedValue([
      { id: 1, title: '2BHK in Hinjewadi', city: 'Pune', price: 7200000, status: 'ACTIVE' },
    ])
    adminApi.getAnalytics.mockResolvedValue({ totalConversations: 5, totalMessages: 20 })

    render(<DashboardPage />)

    expect(await screen.findByText('2BHK in Hinjewadi')).toBeInTheDocument()
    expect(screen.getByText('5')).toBeInTheDocument()
  })

  it('shows an error state when a call fails', async () => {
    adminApi.listUsers.mockRejectedValue(new Error('failed'))
    adminApi.listPropertiesAdmin.mockResolvedValue([])
    adminApi.getAnalytics.mockResolvedValue({ totalConversations: 0, totalMessages: 0 })

    render(<DashboardPage />)

    expect(await screen.findByText('Unable to load dashboard data.')).toBeInTheDocument()
  })
})
