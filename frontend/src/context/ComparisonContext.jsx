import { createContext, useContext, useState } from 'react'

const STORAGE_KEY = 'propertyhub_comparison'
const MAX_ITEMS = 4
const ComparisonContext = createContext(null)

function readStored() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

export function ComparisonProvider({ children }) {
  const [ids, setIds] = useState(readStored)

  function persist(next) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
    setIds(next)
  }

  function toggle(propertyId) {
    if (ids.includes(propertyId)) {
      persist(ids.filter((id) => id !== propertyId))
      return true
    }
    if (ids.length >= MAX_ITEMS) {
      return false
    }
    persist([...ids, propertyId])
    return true
  }

  function clear() {
    persist([])
  }

  const value = {
    ids,
    isSelected: (propertyId) => ids.includes(propertyId),
    isFull: ids.length >= MAX_ITEMS,
    toggle,
    clear,
  }

  return <ComparisonContext.Provider value={value}>{children}</ComparisonContext.Provider>
}

export function useComparison() {
  const ctx = useContext(ComparisonContext)
  if (!ctx) {
    throw new Error('useComparison must be used within a ComparisonProvider')
  }
  return ctx
}

export { MAX_ITEMS, STORAGE_KEY }
