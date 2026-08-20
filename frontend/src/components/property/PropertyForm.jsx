import { useState } from 'react'
import Input from '../common/Input.jsx'
import Select from '../common/Select.jsx'
import Button from '../common/Button.jsx'
import { validateProperty } from '../../validation/propertyValidation.js'

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

const EMPTY_FORM = {
  title: '',
  description: '',
  city: '',
  price: '',
  bhk: '',
  area: '',
  propertyType: 'APARTMENT',
  furnishing: 'UNFURNISHED',
  parking: false,
}

export default function PropertyForm({ initialValues, onSubmit, submitLabel = 'Save' }) {
  const [values, setValues] = useState({ ...EMPTY_FORM, ...initialValues })
  const [errors, setErrors] = useState({})

  function handleChange(field) {
    return (event) => {
      const value = field === 'parking' ? event.target.checked : event.target.value
      setValues((current) => ({ ...current, [field]: value }))
    }
  }

  function handleSubmit(event) {
    event.preventDefault()
    const validationErrors = validateProperty(values)
    setErrors(validationErrors)
    if (Object.keys(validationErrors).length === 0) {
      onSubmit({
        ...values,
        price: Number(values.price),
        bhk: Number(values.bhk),
        area: Number(values.area),
      })
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <Input label="Title" name="title" value={values.title} onChange={handleChange('title')} error={errors.title} />
      <Input label="Description" name="description" value={values.description} onChange={handleChange('description')} />
      <Input label="City" name="city" value={values.city} onChange={handleChange('city')} error={errors.city} />
      <Input label="Price" name="price" type="number" value={values.price} onChange={handleChange('price')} error={errors.price} />
      <Input label="BHK" name="bhk" type="number" value={values.bhk} onChange={handleChange('bhk')} error={errors.bhk} />
      <Input label="Area (sqft)" name="area" type="number" value={values.area} onChange={handleChange('area')} error={errors.area} />
      <Select
        label="Property Type"
        name="propertyType"
        value={values.propertyType}
        onChange={handleChange('propertyType')}
        options={PROPERTY_TYPE_OPTIONS}
        error={errors.propertyType}
      />
      <Select
        label="Furnishing"
        name="furnishing"
        value={values.furnishing}
        onChange={handleChange('furnishing')}
        options={FURNISHING_OPTIONS}
        error={errors.furnishing}
      />
      <label style={{ display: 'block', marginBottom: 'var(--space-md)' }}>
        <input type="checkbox" checked={values.parking} onChange={handleChange('parking')} /> Parking available
      </label>
      <Button type="submit">{submitLabel}</Button>
    </form>
  )
}
