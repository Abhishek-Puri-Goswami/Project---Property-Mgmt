import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import PropertyCard from '../components/property/PropertyCard.jsx'
import ScheduleVisitModal from '../components/property/ScheduleVisitModal.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { useToast } from '../context/ToastContext.jsx'
import { useComparison } from '../context/ComparisonContext.jsx'
import { listFavorites, removeFavorite, scheduleVisit } from '../api/propertyApi.js'

export default function FavoritesPage() {
  const [favorites, setFavorites] = useState([])
  const [status, setStatus] = useState('loading')
  const [visitTarget, setVisitTarget] = useState(null)
  const { user } = useAuth()
  const { showToast } = useToast()
  const { ids: comparedIds, toggle: toggleCompare, isFull } = useComparison()

  function load() {
    setStatus('loading')
    listFavorites(user.id)
      .then((data) => {
        setFavorites(data)
        setStatus(data.length === 0 ? 'empty' : 'success')
      })
      .catch(() => setStatus('error'))
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function handleRemove(property) {
    try {
      await removeFavorite(property.id, user.id)
      showToast('Removed from favorites', 'success')
      load()
    } catch (error) {
      showToast(error.message || 'Unable to update favorites', 'error')
    }
  }

  function handleToggleCompare(property) {
    const added = toggleCompare(property.id)
    if (!added) {
      showToast('You can compare up to 4 properties', 'info')
    }
  }

  async function handleScheduleVisit(request) {
    try {
      await scheduleVisit(visitTarget.id, { ...request, userId: user.id })
      showToast('Visit scheduled successfully', 'success')
      setVisitTarget(null)
    } catch (error) {
      showToast(error.message || 'Unable to schedule visit', 'error')
    }
  }

  return (
    <>
      <Card>
        <h1>Favorites</h1>
      </Card>
      <div style={{ marginTop: 'var(--space-md)' }}>
        {status === 'loading' && <LoadingState />}
        {status === 'error' && <ErrorState message="Unable to load favorites." />}
        {status === 'empty' && <EmptyState message="You haven't favorited any properties yet." />}
        {status === 'success' && favorites.map((favorite) => (
          <PropertyCard
            key={favorite.id}
            property={favorite.property}
            isFavorited
            isCompared={comparedIds.includes(favorite.property.id)}
            compareDisabled={isFull}
            onToggleFavorite={() => handleRemove(favorite.property)}
            onToggleCompare={handleToggleCompare}
            onScheduleVisit={setVisitTarget}
          />
        ))}
      </div>
      <ScheduleVisitModal open={Boolean(visitTarget)} onClose={() => setVisitTarget(null)} onSubmit={handleScheduleVisit} />
    </>
  )
}
