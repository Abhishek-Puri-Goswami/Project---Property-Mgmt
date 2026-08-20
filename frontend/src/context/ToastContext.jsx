import { createContext, useCallback, useContext, useRef, useState } from 'react'

const ToastContext = createContext(null)
const TOAST_TIMEOUT_MS = 4000

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])
  const nextId = useRef(0)

  const dismissToast = useCallback((id) => {
    setToasts((current) => current.filter((toast) => toast.id !== id))
  }, [])

  const showToast = useCallback((message, type = 'info') => {
    const id = nextId.current++
    setToasts((current) => [...current, { id, message, type }])
    setTimeout(() => dismissToast(id), TOAST_TIMEOUT_MS)
    return id
  }, [dismissToast])

  return (
    <ToastContext.Provider value={{ toasts, showToast, dismissToast }}>
      {children}
    </ToastContext.Provider>
  )
}

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) {
    throw new Error('useToast must be used within a ToastProvider')
  }
  return ctx
}

export { TOAST_TIMEOUT_MS }
