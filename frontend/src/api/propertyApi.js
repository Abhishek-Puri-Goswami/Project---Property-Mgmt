import axiosClient from './axiosClient.js'

export function search(params) {
  return axiosClient.get('/api/properties', { params }).then((res) => res.data)
}

export function get(id) {
  return axiosClient.get(`/api/properties/${id}`).then((res) => res.data)
}

export function create(request) {
  return axiosClient.post('/api/properties', request).then((res) => res.data)
}

export function update(id, request) {
  return axiosClient.put(`/api/properties/${id}`, request).then((res) => res.data)
}

export function remove(id) {
  return axiosClient.delete(`/api/properties/${id}`)
}

export function addFavorite(propertyId, userId) {
  return axiosClient.post(`/api/properties/${propertyId}/favorites`, null, { params: { userId } }).then((res) => res.data)
}

export function removeFavorite(propertyId, userId) {
  return axiosClient.delete(`/api/properties/${propertyId}/favorites`, { params: { userId } })
}

export function listFavorites(userId) {
  return axiosClient.get('/api/properties/favorites', { params: { userId } }).then((res) => res.data)
}

export function scheduleVisit(propertyId, request) {
  return axiosClient.post(`/api/properties/${propertyId}/visits`, request).then((res) => res.data)
}

export function listVisitsByUser(userId) {
  return axiosClient.get('/api/properties/visits', { params: { userId } }).then((res) => res.data)
}

export function listVisitsByAgent(agentId) {
  return axiosClient.get('/api/properties/visits', { params: { agentId } }).then((res) => res.data)
}
