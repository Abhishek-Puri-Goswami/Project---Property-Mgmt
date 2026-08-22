import axiosClient from './axiosClient.js'

const ADMIN_SERVER_URL = import.meta.env.VITE_ADMIN_SERVER_URL || 'http://localhost:8084'

export function login(request) {
  return axiosClient.post('/api/auth/login', request).then((res) => res.data)
}

export function listUsers() {
  return axiosClient.get('/api/auth/users').then((res) => res.data)
}

export function listPropertiesAdmin() {
  return axiosClient.get('/api/properties/admin').then((res) => res.data)
}

export function getProperty(id) {
  return axiosClient.get(`/api/properties/${id}`).then((res) => res.data)
}

export function approveProperty(id) {
  return axiosClient.patch(`/api/properties/${id}/approve`).then((res) => res.data)
}

export function updateProperty(id, request) {
  return axiosClient.put(`/api/properties/${id}`, request).then((res) => res.data)
}

export function deleteProperty(id) {
  return axiosClient.delete(`/api/properties/${id}`)
}

export function listConversations() {
  return axiosClient.get('/api/ai/conversations').then((res) => res.data)
}

export function getAnalytics() {
  return axiosClient.get('/api/ai/analytics').then((res) => res.data)
}

export function listServiceInstances() {
  return axiosClient
    .get(`${ADMIN_SERVER_URL}/instances`, { headers: { Accept: 'application/json' } })
    .then((res) => res.data)
}
