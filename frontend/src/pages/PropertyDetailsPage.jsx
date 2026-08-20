import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import Card from '../components/common/Card.jsx'
import Button from '../components/common/Button.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import PropertyForm from '../components/property/PropertyForm.jsx'
import ScheduleVisitModal from '../components/property/ScheduleVisitModal.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { useToast } from '../context/ToastContext.jsx'
import { useComparison } from '../context/ComparisonContext.jsx'
import { get, update, remove, addFavorite, removeFavorite, listFavorites, scheduleVisit } from '../api/propertyApi.js'

export default function PropertyDetailsPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [property, setProperty] = useState(null)
  const [status, setStatus] = useState('loading')
  const [editing, setEditing] = useState(false)
  const [isFavorited, setIsFavorited] = useState(false)
  const [visitOpen, setVisitOpen] = useState(false)
  const { user } = useAuth()
  const { showToast } = useToast()
  const { isSelected, isFull, toggle: toggleCompare } = useComparison()

  function load() {
    setStatus('loading')
    get(id)
      .then((data) => {
        setProperty(data)
        setStatus('success')
      })
      .catch(() => setStatus('error'))
  }

  useEffect(() => {
    load()
    if (user) {
      listFavorites(user.id).then((favorites) => {
        setIsFavorited(favorites.some((favorite) => favorite.property.id === Number(id)))
      })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  async function handleToggleFavorite() {
    try {
      if (isFavorited) {
        await removeFavorite(id, user.id)
        setIsFavorited(false)
        showToast('Removed from favorites', 'success')
      } else {
        await addFavorite(id, user.id)
        setIsFavorited(true)
        showToast('Added to favorites', 'success')
      }
    } catch (error) {
      showToast(error.message || 'Unable to update favorites', 'error')
    }
  }

  function handleToggleCompare() {
    const added = toggleCompare(Number(id))
    if (!added) {
      showToast('You can compare up to 4 properties', 'info')
    }
  }

  async function handleScheduleVisit(request) {
    try {
      await scheduleVisit(id, { ...request, userId: user.id })
      showToast('Visit scheduled successfully', 'success')
      setVisitOpen(false)
    } catch (error) {
      showToast(error.message || 'Unable to schedule visit', 'error')
    }
  }

  async function handleUpdate(values) {
    try {
      const updated = await update(id, { ...values, agentId: property.agentId })
      setProperty(updated)
      setEditing(false)
      showToast('Property updated successfully', 'success')
    } catch (error) {
      showToast(error.message || 'Unable to update property', 'error')
    }
  }

  async function handleDelete() {
    try {
      await remove(id)
      showToast('Property deleted successfully', 'success')
      navigate('/properties')
    } catch (error) {
      showToast(error.message || 'Unable to delete property', 'error')
    }
  }

  if (status === 'loading') {
    return <LoadingState />
  }
  if (status === 'error' || !property) {
    return <ErrorState message="Unable to load this property." />
  }

  const isOwner = user?.role === 'AGENT' && user.id === property.agentId

  if (editing) {
    return (
      <Card>
        <h1>Edit Property</h1>
        <PropertyForm initialValues={property} onSubmit={handleUpdate} submitLabel="Save Changes" />
        <Button variant="secondary" onClick={() => setEditing(false)}>Cancel</Button>
      </Card>
    )
  }

  return (
    <Card>
      <h1>{property.title}</h1>
      <p style={{ color: 'var(--color-text-muted)' }}>
        {property.city} &middot; {property.bhk} BHK &middot; {property.area} sqft &middot; {property.propertyType} &middot; {property.furnishing}
      </p>
      <p style={{ fontWeight: 700, fontSize: '1.3rem' }}>₹{Number(property.price).toLocaleString('en-IN')}</p>
      {property.description && <p>{property.description}</p>}
      <p>Parking: {property.parking ? 'Yes' : 'No'}</p>

      <div style={{ display: 'flex', gap: 'var(--space-sm)', flexWrap: 'wrap', marginTop: 'var(--space-md)' }}>
        <Button variant={isFavorited ? 'primary' : 'secondary'} onClick={handleToggleFavorite}>
          {isFavorited ? 'Unfavorite' : 'Favorite'}
        </Button>
        <Button
          variant={isSelected(Number(id)) ? 'primary' : 'secondary'}
          onClick={handleToggleCompare}
          disabled={!isSelected(Number(id)) && isFull}
        >
          {isSelected(Number(id)) ? 'Remove from Compare' : 'Compare'}
        </Button>
        <Button variant="secondary" onClick={() => setVisitOpen(true)}>Schedule Visit</Button>
        <Link to="/ai-copilot"><Button variant="secondary">Ask AI</Button></Link>
        {isOwner && <Button variant="secondary" onClick={() => setEditing(true)}>Edit</Button>}
        {isOwner && <Button variant="secondary" onClick={handleDelete}>Delete</Button>}
      </div>

      <ScheduleVisitModal open={visitOpen} onClose={() => setVisitOpen(false)} onSubmit={handleScheduleVisit} />
    </Card>
  )
}
