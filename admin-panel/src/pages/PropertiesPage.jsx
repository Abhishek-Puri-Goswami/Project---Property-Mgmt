import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import Button from '../components/common/Button.jsx'
import Input from '../components/common/Input.jsx'
import Select from '../components/common/Select.jsx'
import Modal from '../components/common/Modal.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import Table from '../components/common/Table.jsx'
import { useToast } from '../context/ToastContext.jsx'
import { listPropertiesAdmin, getProperty, approveProperty, updateProperty, deleteProperty } from '../api/adminApi.js'

const PROPERTY_TYPE_OPTIONS = [
  { value: 'APARTMENT', label: 'Apartment' },
  { value: 'VILLA', label: 'Villa' },
  { value: 'INDEPENDENT_HOUSE', label: 'Independent House' },
  { value: 'PLOT', label: 'Plot' },
  { value: 'COMMERCIAL', label: 'Commercial' },
]

const FURNISHING_OPTIONS = [
  { value: 'FURNISHED', label: 'Furnished' },
  { value: 'SEMI_FURNISHED', label: 'Semi-Furnished' },
  { value: 'UNFURNISHED', label: 'Unfurnished' },
]

export default function PropertiesPage() {
  const [properties, setProperties] = useState([])
  const [status, setStatus] = useState('loading')
  const [editing, setEditing] = useState(null)
  const { showToast } = useToast()

  function load() {
    setStatus('loading')
    listPropertiesAdmin()
      .then((data) => {
        setProperties(data)
        setStatus(data.length === 0 ? 'empty' : 'success')
      })
      .catch(() => setStatus('error'))
  }

  useEffect(() => {
    load()
  }, [])

  async function handleApprove(id) {
    try {
      await approveProperty(id)
      showToast('Property approved successfully', 'success')
      load()
    } catch (error) {
      showToast(error.message || 'Unable to approve property', 'error')
    }
  }

  async function handleDelete(id) {
    try {
      await deleteProperty(id)
      showToast('Property deleted successfully', 'success')
      load()
    } catch (error) {
      showToast(error.message || 'Unable to delete property', 'error')
    }
  }

  async function handleEditClick(id) {
    try {
      const property = await getProperty(id)
      setEditing(property)
    } catch (error) {
      showToast(error.message || 'Unable to load property', 'error')
    }
  }

  async function handleSaveEdit(event) {
    event.preventDefault()
    try {
      await updateProperty(editing.id, editing)
      showToast('Property updated successfully', 'success')
      setEditing(null)
      load()
    } catch (error) {
      showToast(error.message || 'Unable to update property', 'error')
    }
  }

  function updateEditingField(field) {
    return (event) => {
      const value = field === 'parking' ? event.target.checked : event.target.value
      setEditing((current) => ({ ...current, [field]: value }))
    }
  }

  return (
    <Card>
      <h1>Properties</h1>
      {status === 'loading' && <LoadingState />}
      {status === 'error' && <ErrorState message="Unable to load properties." />}
      {status === 'empty' && <EmptyState message="No properties found." />}
      {status === 'success' && (
        <Table
          rowKey="id"
          rows={properties}
          columns={[
            { key: 'title', header: 'Property' },
            { key: 'city', header: 'Location' },
            { key: 'price', header: 'Price', render: (p) => `₹${Number(p.price).toLocaleString('en-IN')}` },
            { key: 'status', header: 'Status' },
            {
              key: 'actions',
              header: 'Actions',
              render: (p) => (
                <div style={{ display: 'flex', gap: 'var(--space-sm)' }}>
                  <Button variant="secondary" onClick={() => handleEditClick(p.id)}>Edit</Button>
                  {p.status === 'PENDING' && (
                    <Button onClick={() => handleApprove(p.id)}>Approve</Button>
                  )}
                  <Button variant="secondary" onClick={() => handleDelete(p.id)}>Delete</Button>
                </div>
              ),
            },
          ]}
        />
      )}

      <Modal open={Boolean(editing)} title="Edit Property" onClose={() => setEditing(null)}>
        {editing && (
          <form onSubmit={handleSaveEdit}>
            <Input label="Title" name="title" value={editing.title} onChange={updateEditingField('title')} />
            <Input label="Description" name="description" value={editing.description || ''} onChange={updateEditingField('description')} />
            <Input label="City" name="city" value={editing.city} onChange={updateEditingField('city')} />
            <Input label="Price" name="price" type="number" value={editing.price} onChange={updateEditingField('price')} />
            <Input label="BHK" name="bhk" type="number" value={editing.bhk} onChange={updateEditingField('bhk')} />
            <Input label="Area (sqft)" name="area" type="number" value={editing.area} onChange={updateEditingField('area')} />
            <Select label="Property Type" name="propertyType" value={editing.propertyType} onChange={updateEditingField('propertyType')} options={PROPERTY_TYPE_OPTIONS} />
            <Select label="Furnishing" name="furnishing" value={editing.furnishing} onChange={updateEditingField('furnishing')} options={FURNISHING_OPTIONS} />
            <label style={{ display: 'block', marginBottom: 'var(--space-md)' }}>
              <input type="checkbox" checked={editing.parking} onChange={updateEditingField('parking')} /> Parking available
            </label>
            <Button type="submit">Save Changes</Button>
          </form>
        )}
      </Modal>
    </Card>
  )
}
