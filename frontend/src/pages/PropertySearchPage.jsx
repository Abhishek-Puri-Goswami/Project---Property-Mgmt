import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import Input from '../components/common/Input.jsx'
import Select from '../components/common/Select.jsx'
import Button from '../components/common/Button.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import PropertyCard from '../components/property/PropertyCard.jsx'
import ScheduleVisitModal from '../components/property/ScheduleVisitModal.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { useToast } from '../context/ToastContext.jsx'
import { useComparison } from '../context/ComparisonContext.jsx'
import { search, addFavorite, removeFavorite, listFavorites, scheduleVisit } from '../api/propertyApi.js'

const PROPERTY_TYPE_OPTIONS = [
  { value: '', label: 'Any type' },
  { value: 'APARTMENT', label: 'Apartment' },
  { value: 'VILLA', label: 'Villa' },
  { value: 'INDEPENDENT_HOUSE', label: 'Independent House' },
  { value: 'PLOT', label: 'Plot' },
  { value: 'COMMERCIAL', label: 'Commercial' },
]

const FURNISHING_OPTIONS = [
  { value: '', label: 'Any furnishing' },
  { value: 'FURNISHED', label: 'Furnished' },
  { value: 'SEMI_FURNISHED', label: 'Semi-Furnished' },
  { value: 'UNFURNISHED', label: 'Unfurnished' },
]

export default function PropertySearchPage() {
  const [filters, setFilters] = useState({ city: '', bhk: '', minPrice: '', maxPrice: '', propertyType: '', furnishing: '' })
  const [results, setResults] = useState([])
  const [status, setStatus] = useState('idle')
  const [favoritedIds, setFavoritedIds] = useState(new Set())
  const [visitTarget, setVisitTarget] = useState(null)
  const { user } = useAuth()
  const { showToast } = useToast()
  const { ids: comparedIds, toggle: toggleCompare, isFull } = useComparison()

  function runSearch(activeFilters) {
    setStatus('loading')
    const params = Object.fromEntries(Object.entries(activeFilters).filter(([, value]) => value !== ''))
    search(params)
      .then((data) => {
        setResults(data)
        setStatus(data.length === 0 ? 'empty' : 'success')
      })
      .catch(() => {
        setStatus('error')
        showToast('Unable to load properties', 'error')
      })
  }

  useEffect(() => {
    runSearch(filters)
    if (user) {
      listFavorites(user.id).then((favorites) => {
        setFavoritedIds(new Set(favorites.map((favorite) => favorite.property.id)))
      })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function handleFilterChange(field) {
    return (event) => setFilters((current) => ({ ...current, [field]: event.target.value }))
  }

  function handleSearchSubmit(event) {
    event.preventDefault()
    runSearch(filters)
  }

  async function handleToggleFavorite(property) {
    try {
      if (favoritedIds.has(property.id)) {
        await removeFavorite(property.id, user.id)
        setFavoritedIds((current) => {
          const next = new Set(current)
          next.delete(property.id)
          return next
        })
        showToast('Removed from favorites', 'success')
      } else {
        await addFavorite(property.id, user.id)
        setFavoritedIds((current) => new Set(current).add(property.id))
        showToast('Added to favorites', 'success')
      }
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
        <h1>Property Search</h1>
        <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: 'var(--space-md)', flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <Input label="City" name="city" value={filters.city} onChange={handleFilterChange('city')} />
          <Input label="BHK" name="bhk" type="number" value={filters.bhk} onChange={handleFilterChange('bhk')} />
          <Input label="Min Price" name="minPrice" type="number" value={filters.minPrice} onChange={handleFilterChange('minPrice')} />
          <Input label="Max Price" name="maxPrice" type="number" value={filters.maxPrice} onChange={handleFilterChange('maxPrice')} />
          <Select label="Type" name="propertyType" value={filters.propertyType} onChange={handleFilterChange('propertyType')} options={PROPERTY_TYPE_OPTIONS} />
          <Select label="Furnishing" name="furnishing" value={filters.furnishing} onChange={handleFilterChange('furnishing')} options={FURNISHING_OPTIONS} />
          <Button type="submit">Search</Button>
        </form>
      </Card>

      <div style={{ marginTop: 'var(--space-md)' }}>
        {status === 'loading' && <LoadingState />}
        {status === 'error' && <ErrorState message="Unable to load properties." />}
        {status === 'empty' && <EmptyState message="No properties match your search." />}
        {status === 'success' && results.map((property) => (
          <PropertyCard
            key={property.id}
            property={property}
            isFavorited={favoritedIds.has(property.id)}
            isCompared={comparedIds.includes(property.id)}
            compareDisabled={isFull}
            onToggleFavorite={handleToggleFavorite}
            onToggleCompare={handleToggleCompare}
            onScheduleVisit={setVisitTarget}
          />
        ))}
      </div>

      <ScheduleVisitModal open={Boolean(visitTarget)} onClose={() => setVisitTarget(null)} onSubmit={handleScheduleVisit} />
    </>
  )
}
