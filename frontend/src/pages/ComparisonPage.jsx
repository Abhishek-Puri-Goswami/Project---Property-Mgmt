import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import Button from '../components/common/Button.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import { useComparison } from '../context/ComparisonContext.jsx'
import { get } from '../api/propertyApi.js'

export default function ComparisonPage() {
  const { ids, clear, toggle } = useComparison()
  const [properties, setProperties] = useState([])
  const [status, setStatus] = useState('idle')

  useEffect(() => {
    if (ids.length === 0) {
      setProperties([])
      setStatus('empty')
      return
    }
    setStatus('loading')
    Promise.all(ids.map((id) => get(id)))
      .then((data) => {
        setProperties(data)
        setStatus('success')
      })
      .catch(() => setStatus('error'))
  }, [ids])

  const rows = [
    { key: 'city', header: 'City', render: (p) => p.city },
    { key: 'price', header: 'Price', render: (p) => `₹${Number(p.price).toLocaleString('en-IN')}` },
    { key: 'bhk', header: 'BHK', render: (p) => p.bhk },
    { key: 'area', header: 'Area', render: (p) => `${p.area} sqft` },
    { key: 'propertyType', header: 'Type', render: (p) => p.propertyType },
    { key: 'furnishing', header: 'Furnishing', render: (p) => p.furnishing },
    { key: 'parking', header: 'Parking', render: (p) => (p.parking ? 'Yes' : 'No') },
    { key: 'remove', header: '', render: (p) => <Button variant="secondary" onClick={() => toggle(p.id)}>Remove</Button> },
  ]

  return (
    <Card>
      <h1>Comparison</h1>
      {status === 'loading' && <LoadingState />}
      {status === 'error' && <ErrorState message="Unable to load compared properties." />}
      {status === 'empty' && <EmptyState message="Add properties to compare from Search or Details." />}
      {status === 'success' && (
        <>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={{ textAlign: 'left', padding: 'var(--space-sm)' }}></th>
                  {properties.map((p) => (
                    <th key={p.id} style={{ textAlign: 'left', padding: 'var(--space-sm)' }}>{p.title}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.key}>
                    <td style={{ padding: 'var(--space-sm)', fontWeight: 600 }}>{row.header}</td>
                    {properties.map((p) => (
                      <td key={p.id} style={{ padding: 'var(--space-sm)' }}>{row.render(p)}</td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Button variant="secondary" onClick={clear}>Clear Comparison</Button>
        </>
      )}
    </Card>
  )
}
