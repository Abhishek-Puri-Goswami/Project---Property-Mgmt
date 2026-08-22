import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import * as adminApi from '../api/adminApi.js'
import AiAnalyticsPage from './AiAnalyticsPage.jsx'

vi.mock('../api/adminApi.js')

describe('AiAnalyticsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders totals and conversation list', async () => {
    adminApi.getAnalytics.mockResolvedValue({ totalConversations: 3, totalMessages: 10 })
    adminApi.listConversations.mockResolvedValue([
      { id: 1, userId: 5, title: 'Find me a 2 BHK', messageCount: 4, updatedAt: '2026-08-21T00:00:00' },
    ])

    render(<AiAnalyticsPage />)

    expect(await screen.findByText('3')).toBeInTheDocument()
    expect(screen.getByText('Find me a 2 BHK')).toBeInTheDocument()
  })

  it('shows an empty state when there are no conversations', async () => {
    adminApi.getAnalytics.mockResolvedValue({ totalConversations: 0, totalMessages: 0 })
    adminApi.listConversations.mockResolvedValue([])

    render(<AiAnalyticsPage />)

    expect(await screen.findByText('No conversations yet.')).toBeInTheDocument()
  })

  it('shows an error state when a call fails', async () => {
    adminApi.getAnalytics.mockRejectedValue(new Error('failed'))
    adminApi.listConversations.mockResolvedValue([])

    render(<AiAnalyticsPage />)

    expect(await screen.findByText('Unable to load AI analytics.')).toBeInTheDocument()
  })
})
