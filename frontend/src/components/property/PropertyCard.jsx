import { Link } from 'react-router-dom'
import Card from '../common/Card.jsx'
import Button from '../common/Button.jsx'

export default function PropertyCard({ property, isFavorited, isCompared, compareDisabled, onToggleFavorite, onToggleCompare, onScheduleVisit }) {
  return (
    <Card style={{ marginBottom: 'var(--space-md)' }}>
      <h3 style={{ marginTop: 0 }}>
        <Link to={`/properties/${property.id}`}>{property.title}</Link>
      </h3>
      <p style={{ color: 'var(--color-text-muted)', margin: '4px 0' }}>
        {property.city} &middot; {property.bhk} BHK &middot; {property.area} sqft &middot; {property.propertyType}
      </p>
      <p style={{ fontWeight: 700, fontSize: '1.1rem' }}>₹{Number(property.price).toLocaleString('en-IN')}</p>
      <div style={{ display: 'flex', gap: 'var(--space-sm)', flexWrap: 'wrap' }}>
        <Button variant={isFavorited ? 'primary' : 'secondary'} onClick={() => onToggleFavorite(property)}>
          {isFavorited ? 'Unfavorite' : 'Favorite'}
        </Button>
        <Button
          variant={isCompared ? 'primary' : 'secondary'}
          onClick={() => onToggleCompare(property)}
          disabled={!isCompared && compareDisabled}
        >
          {isCompared ? 'Remove from Compare' : 'Compare'}
        </Button>
        <Button variant="secondary" onClick={() => onScheduleVisit(property)}>Schedule Visit</Button>
        <Link to="/ai-copilot">
          <Button variant="secondary">Ask AI</Button>
        </Link>
      </div>
    </Card>
  )
}
