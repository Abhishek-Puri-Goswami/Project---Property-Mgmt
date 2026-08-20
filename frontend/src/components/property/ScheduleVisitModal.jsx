import { useState } from 'react'
import Modal from '../common/Modal.jsx'
import Input from '../common/Input.jsx'
import Button from '../common/Button.jsx'
import { validateVisit } from '../../validation/propertyValidation.js'

export default function ScheduleVisitModal({ open, onClose, onSubmit }) {
  const [scheduledAt, setScheduledAt] = useState('')
  const [notes, setNotes] = useState('')
  const [errors, setErrors] = useState({})

  function handleSubmit(event) {
    event.preventDefault()
    const validationErrors = validateVisit({ scheduledAt })
    setErrors(validationErrors)
    if (Object.keys(validationErrors).length === 0) {
      onSubmit({ scheduledAt: new Date(scheduledAt).toISOString(), notes })
    }
  }

  return (
    <Modal open={open} title="Schedule a Visit" onClose={onClose}>
      <form onSubmit={handleSubmit}>
        <Input
          label="Date and Time"
          name="scheduledAt"
          type="datetime-local"
          value={scheduledAt}
          onChange={(event) => setScheduledAt(event.target.value)}
          error={errors.scheduledAt}
        />
        <Input label="Notes" name="notes" value={notes} onChange={(event) => setNotes(event.target.value)} />
        <Button type="submit">Schedule</Button>
      </form>
    </Modal>
  )
}
