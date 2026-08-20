export function validateProperty({ title, city, price, bhk, area, propertyType, furnishing }) {
  const errors = {}

  if (!title || !title.trim()) {
    errors.title = 'Title is required'
  }

  if (!city || !city.trim()) {
    errors.city = 'City is required'
  }

  if (price === undefined || price === null || price === '') {
    errors.price = 'Price is required'
  } else if (Number(price) <= 0) {
    errors.price = 'Price must be positive'
  }

  if (bhk === undefined || bhk === null || bhk === '') {
    errors.bhk = 'BHK is required'
  } else if (Number(bhk) <= 0) {
    errors.bhk = 'BHK must be positive'
  }

  if (area === undefined || area === null || area === '') {
    errors.area = 'Area is required'
  } else if (Number(area) <= 0) {
    errors.area = 'Area must be positive'
  }

  if (!propertyType) {
    errors.propertyType = 'Property type is required'
  }

  if (!furnishing) {
    errors.furnishing = 'Furnishing is required'
  }

  return errors
}

export function validateVisit({ scheduledAt }) {
  const errors = {}

  if (!scheduledAt) {
    errors.scheduledAt = 'Scheduled time is required'
  } else if (new Date(scheduledAt).getTime() <= Date.now()) {
    errors.scheduledAt = 'Scheduled time must be in the future'
  }

  return errors
}
