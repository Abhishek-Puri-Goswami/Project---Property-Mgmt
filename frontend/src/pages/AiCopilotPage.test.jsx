import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider, STORAGE_KEY } from '../context/AuthContext.jsx'
import { ToastProvider } from '../context/ToastContext.jsx'
import { ComparisonProvider } from '../context/ComparisonContext.jsx'
import Toast from '../components/common/Toast.jsx'
import * as aiApi from '../api/aiApi.js'
import * as propertyApi from '../api/propertyApi.js'
import AiCopilotPage from './AiCopilotPage.jsx'

vi.mock('../api/aiApi.js')
vi.mock('../api/propertyApi.js')

function renderPage() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ user: { id: 5, email: 'buyer@example.com', role: 'BUYER' }, token: 'jwt-token' }))
  return render(
    <AuthProvider>
      <ToastProvider>
        <ComparisonProvider>
          <MemoryRouter>
            <AiCopilotPage />
            <Toast />
          </MemoryRouter>
        </ComparisonProvider>
      </ToastProvider>
    </AuthProvider>
  )
}

function sendMessage(text) {
  fireEvent.change(screen.getByPlaceholderText('Ask about properties, localities, or your budget...'), { target: { value: text } })
  fireEvent.click(screen.getByText('Send'))
}

describe('AiCopilotPage', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('shows an empty state before the first message', () => {
    renderPage()
    expect(screen.getByText('Ask me anything about finding your next property.')).toBeInTheDocument()
  })

  it('sends a message and renders the AI reply', async () => {
    aiApi.chat.mockResolvedValue({ conversationId: 1, messageId: 10, response: 'I found 3 matching properties.' })
    aiApi.extractRequirement.mockResolvedValue({ city: null, bhk: null, maxBudget: null, parkingRequired: null })
    renderPage()

    sendMessage('Find me a 2 BHK in Pune under 80 lakh')

    expect(await screen.findByText('I found 3 matching properties.')).toBeInTheDocument()
  })

  it('reuses the conversationId for a follow-up message', async () => {
    aiApi.chat.mockResolvedValue({ conversationId: 7, messageId: 10, response: 'Reply one' })
    aiApi.extractRequirement.mockResolvedValue({ city: null, bhk: null, maxBudget: null, parkingRequired: null })
    renderPage()

    sendMessage('First message')
    await screen.findByText('Reply one')

    aiApi.chat.mockResolvedValue({ conversationId: 7, messageId: 11, response: 'Reply two' })
    sendMessage('Follow-up message')
    await screen.findByText('Reply two')

    expect(aiApi.chat).toHaveBeenLastCalledWith({ conversationId: 7, userId: 5, message: 'Follow-up message' })
  })

  it('renders property cards when extraction yields a criterion and results are found', async () => {
    aiApi.chat.mockResolvedValue({ conversationId: 1, messageId: 10, response: 'Here is a match.' })
    aiApi.extractRequirement.mockResolvedValue({ city: 'Pune', bhk: 2, maxBudget: 8000000, parkingRequired: true })
    propertyApi.search.mockResolvedValue([
      { id: 1, title: '2BHK in Hinjewadi', city: 'Pune', price: 7200000, bhk: 2, area: 1150, propertyType: 'APARTMENT' },
    ])
    propertyApi.listFavorites.mockResolvedValue([])
    renderPage()

    sendMessage('Find me a 2 BHK in Pune under 80 lakh')

    expect(await screen.findByText('2BHK in Hinjewadi')).toBeInTheDocument()
  })

  it('renders no property cards when extraction yields no criterion', async () => {
    aiApi.chat.mockResolvedValue({ conversationId: 1, messageId: 10, response: 'Hello there.' })
    aiApi.extractRequirement.mockResolvedValue({ city: null, bhk: null, maxBudget: null, parkingRequired: null })
    renderPage()

    sendMessage('Hi')
    await screen.findByText('Hello there.')

    expect(propertyApi.search).not.toHaveBeenCalled()
  })

  it('shows an error toast when the chat call fails', async () => {
    aiApi.chat.mockRejectedValue({ message: 'AI request failed' })
    renderPage()

    sendMessage('Find me a property')

    expect(await screen.findByText('AI request failed')).toBeInTheDocument()
  })
})
