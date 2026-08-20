import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider, STORAGE_KEY } from '../context/AuthContext.jsx'
import { ToastProvider } from '../context/ToastContext.jsx'
import { ComparisonProvider } from '../context/ComparisonContext.jsx'
import * as propertyApi from '../api/propertyApi.js'
import PropertyDetailsPage from './PropertyDetailsPage.jsx'

vi.mock('../api/propertyApi.js')

function renderPage(user) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ user, token: 'jwt-token' }))
  return render(
    <AuthProvider>
      <ToastProvider>
        <ComparisonProvider>
          <MemoryRouter initialEntries={['/properties/1']}>
            <Routes>
              <Route path="/properties/:id" element={<PropertyDetailsPage />} />
            </Routes>
          </MemoryRouter>
        </ComparisonProvider>
      </ToastProvider>
    </AuthProvider>
  )
}

const sampleProperty = {
  id: 1, title: '2BHK in Hinjewadi', description: 'Spacious flat', city: 'Pune', price: 7200000,
  bhk: 2, area: 1150, propertyType: 'APARTMENT', furnishing: 'SEMI_FURNISHED', parking: true, agentId: 9,
}

describe('PropertyDetailsPage', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
    propertyApi.get.mockResolvedValue(sampleProperty)
    propertyApi.listFavorites.mockResolvedValue([])
  })

  it('renders property details', async () => {
    renderPage({ id: 5, email: 'buyer@example.com', role: 'BUYER' })

    expect(await screen.findByText('2BHK in Hinjewadi')).toBeInTheDocument()
  })

  it('calls the favorite API when the Favorite button is clicked', async () => {
    propertyApi.addFavorite.mockResolvedValue({})
    renderPage({ id: 5, email: 'buyer@example.com', role: 'BUYER' })

    await screen.findByText('2BHK in Hinjewadi')
    fireEvent.click(screen.getByText('Favorite'))

    await waitFor(() => expect(propertyApi.addFavorite).toHaveBeenCalledWith('1', 5))
  })

  it('hides Edit/Delete for a non-owning viewer', async () => {
    renderPage({ id: 5, email: 'buyer@example.com', role: 'BUYER' })
    await screen.findByText('2BHK in Hinjewadi')
    expect(screen.queryByText('Edit')).not.toBeInTheDocument()
  })

  it('shows Edit/Delete for the owning agent', async () => {
    renderPage({ id: 9, email: 'agent@example.com', role: 'AGENT' })
    await screen.findByText('2BHK in Hinjewadi')
    expect(screen.getByText('Edit')).toBeInTheDocument()
    expect(screen.getByText('Delete')).toBeInTheDocument()
  })
})
