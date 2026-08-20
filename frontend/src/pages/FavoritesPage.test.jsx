import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider, STORAGE_KEY } from '../context/AuthContext.jsx'
import { ToastProvider } from '../context/ToastContext.jsx'
import { ComparisonProvider } from '../context/ComparisonContext.jsx'
import * as propertyApi from '../api/propertyApi.js'
import FavoritesPage from './FavoritesPage.jsx'

vi.mock('../api/propertyApi.js')

function renderPage() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ user: { id: 5, email: 'buyer@example.com', role: 'BUYER' }, token: 'jwt-token' }))
  return render(
    <AuthProvider>
      <ToastProvider>
        <ComparisonProvider>
          <MemoryRouter>
            <FavoritesPage />
          </MemoryRouter>
        </ComparisonProvider>
      </ToastProvider>
    </AuthProvider>
  )
}

const sampleFavorite = {
  id: 10,
  property: { id: 1, title: '2BHK in Hinjewadi', city: 'Pune', price: 7200000, bhk: 2, area: 1150, propertyType: 'APARTMENT' },
  createdAt: '2026-08-21T00:00:00',
}

describe('FavoritesPage', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('shows an empty state when there are no favorites', async () => {
    propertyApi.listFavorites.mockResolvedValue([])
    renderPage()

    expect(await screen.findByText("You haven't favorited any properties yet.")).toBeInTheDocument()
  })

  it('renders favorited properties and removes one', async () => {
    propertyApi.listFavorites.mockResolvedValueOnce([sampleFavorite]).mockResolvedValueOnce([])
    propertyApi.removeFavorite.mockResolvedValue({})
    renderPage()

    await screen.findByText('2BHK in Hinjewadi')
    fireEvent.click(screen.getByText('Unfavorite'))

    await waitFor(() => expect(propertyApi.removeFavorite).toHaveBeenCalledWith(1, 5))
  })
})
