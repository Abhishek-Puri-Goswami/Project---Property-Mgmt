import axiosClient from './axiosClient.js'

export function chat(request) {
  return axiosClient.post('/api/ai/chat', request).then((res) => res.data)
}

export function extractRequirement(request) {
  return axiosClient.post('/api/ai/requirements', request).then((res) => res.data)
}

export function getConversation(id) {
  return axiosClient.get(`/api/ai/conversations/${id}`).then((res) => res.data)
}
