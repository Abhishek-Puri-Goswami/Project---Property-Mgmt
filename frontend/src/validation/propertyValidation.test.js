import { describe, it, expect } from 'vitest'
import { validateProperty, validateVisit } from './propertyValidation.js'

describe('validateProperty', () => {
  const validProperty = {
    title: '2BHK in Hinjewadi',
    city: 'Pune',
    price: 7200000,
    bhk: 2,
    area: 1150,
    propertyType: 'APARTMENT',
    furnishing: 'SEMI_FURNISHED',
  }

  it('returns no errors for valid input', () => {
    expect(validateProperty(validProperty)).toEqual({})
  })

  it('flags a blank title and city', () => {
    const errors = validateProperty({ ...validProperty, title: '', city: '' })
    expect(errors.title).toBeDefined()
    expect(errors.city).toBeDefined()
  })

  it('flags a non-positive price, bhk, and area', () => {
    const errors = validateProperty({ ...validProperty, price: -1, bhk: 0, area: 0 })
    expect(errors.price).toBeDefined()
    expect(errors.bhk).toBeDefined()
    expect(errors.area).toBeDefined()
  })

  it('flags a missing propertyType and furnishing', () => {
    const errors = validateProperty({ ...validProperty, propertyType: '', furnishing: '' })
    expect(errors.propertyType).toBeDefined()
    expect(errors.furnishing).toBeDefined()
  })
})

describe('validateVisit', () => {
  it('returns no errors for a future date', () => {
    const future = new Date(Date.now() + 86400000).toISOString().slice(0, 16)
    expect(validateVisit({ scheduledAt: future })).toEqual({})
  })

  it('flags a missing scheduledAt', () => {
    expect(validateVisit({ scheduledAt: '' }).scheduledAt).toBeDefined()
  })

  it('flags a past scheduledAt', () => {
    const past = new Date(Date.now() - 86400000).toISOString().slice(0, 16)
    expect(validateVisit({ scheduledAt: past }).scheduledAt).toBeDefined()
  })
})
