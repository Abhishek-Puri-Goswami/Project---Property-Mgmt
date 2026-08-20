import axiosClient from './axiosClient.js'

export function register(request) {
  return axiosClient.post('/api/auth/register', request).then((res) => res.data)
}

export function login(request) {
  return axiosClient.post('/api/auth/login', request).then((res) => res.data)
}
