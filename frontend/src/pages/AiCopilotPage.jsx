import { useState } from 'react'
import Card from '../components/common/Card.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import PropertyCard from '../components/property/PropertyCard.jsx'
import ScheduleVisitModal from '../components/property/ScheduleVisitModal.jsx'
import ChatMessageBubble from '../components/ai/ChatMessageBubble.jsx'
import ChatInput from '../components/ai/ChatInput.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { useToast } from '../context/ToastContext.jsx'
import { useComparison } from '../context/ComparisonContext.jsx'
import { chat, extractRequirement } from '../api/aiApi.js'
import { search, addFavorite, removeFavorite, listFavorites, scheduleVisit } from '../api/propertyApi.js'

export default function AiCopilotPage() {
  const [messages, setMessages] = useState([])
  const [conversationId, setConversationId] = useState(null)
  const [loading, setLoading] = useState(false)
  const [matchedProperties, setMatchedProperties] = useState([])
  const [favoritedIds, setFavoritedIds] = useState(new Set())
  const [visitTarget, setVisitTarget] = useState(null)
  const { user } = useAuth()
  const { showToast } = useToast()
  const { ids: comparedIds, toggle: toggleCompare, isFull } = useComparison()

  async function fetchMatches(message) {
    try {
      const requirement = await extractRequirement({ conversationId, userId: user.id, message })
      const hasCriterion = requirement.city || requirement.bhk || requirement.maxBudget || requirement.parkingRequired
      if (!hasCriterion) {
        setMatchedProperties([])
        return
      }
      const results = await search({
        city: requirement.city || undefined,
        bhk: requirement.bhk || undefined,
        maxPrice: requirement.maxBudget || undefined,
        parking: requirement.parkingRequired || undefined,
      })
      setMatchedProperties(results)
      if (user && results.length > 0) {
        const favorites = await listFavorites(user.id)
        setFavoritedIds(new Set(favorites.map((favorite) => favorite.property.id)))
      }
    } catch {
      setMatchedProperties([])
    }
  }

  async function handleSend(text) {
    setMessages((current) => [...current, { role: 'USER', content: text }])
    setLoading(true)
    try {
      const response = await chat({ conversationId, userId: user.id, message: text })
      setConversationId(response.conversationId)
      setMessages((current) => [...current, { role: 'ASSISTANT', content: response.response }])
      await fetchMatches(text)
    } catch (error) {
      showToast(error.message || 'AI request failed', 'error')
    } finally {
      setLoading(false)
    }
  }

  async function handleToggleFavorite(property) {
    try {
      if (favoritedIds.has(property.id)) {
        await removeFavorite(property.id, user.id)
        setFavoritedIds((current) => {
          const next = new Set(current)
          next.delete(property.id)
          return next
        })
        showToast('Removed from favorites', 'success')
      } else {
        await addFavorite(property.id, user.id)
        setFavoritedIds((current) => new Set(current).add(property.id))
        showToast('Added to favorites', 'success')
      }
    } catch (error) {
      showToast(error.message || 'Unable to update favorites', 'error')
    }
  }

  function handleToggleCompare(property) {
    const added = toggleCompare(property.id)
    if (!added) {
      showToast('You can compare up to 4 properties', 'info')
    }
  }

  async function handleScheduleVisit(request) {
    try {
      await scheduleVisit(visitTarget.id, { ...request, userId: user.id })
      showToast('Visit scheduled successfully', 'success')
      setVisitTarget(null)
    } catch (error) {
      showToast(error.message || 'Unable to schedule visit', 'error')
    }
  }

  return (
    <Card>
      <h1>Ask AI</h1>

      {messages.length === 0 && <EmptyState message="Ask me anything about finding your next property." />}

      {messages.map((message, index) => (
        <ChatMessageBubble key={index} role={message.role} content={message.content} />
      ))}

      {loading && <LoadingState message="Thinking..." />}

      {matchedProperties.length > 0 && (
        <div style={{ marginTop: 'var(--space-md)' }}>
          {matchedProperties.map((property) => (
            <PropertyCard
              key={property.id}
              property={property}
              isFavorited={favoritedIds.has(property.id)}
              isCompared={comparedIds.includes(property.id)}
              compareDisabled={isFull}
              onToggleFavorite={handleToggleFavorite}
              onToggleCompare={handleToggleCompare}
              onScheduleVisit={setVisitTarget}
            />
          ))}
        </div>
      )}

      <div style={{ marginTop: 'var(--space-md)' }}>
        <ChatInput onSend={handleSend} loading={loading} />
      </div>

      <ScheduleVisitModal open={Boolean(visitTarget)} onClose={() => setVisitTarget(null)} onSubmit={handleScheduleVisit} />
    </Card>
  )
}
