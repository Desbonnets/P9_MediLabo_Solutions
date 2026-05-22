import { useState } from 'react'
import { createNote } from '../api/notesApi'

export default function NotesPanel({ patient, notes, onNoteAdded }) {
  const [content, setContent] = useState('')
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    if (!content.trim()) return
    setSubmitting(true)
    setError(null)
    try {
      await createNote({ patId: patient.id, content: content.trim() })
      setContent('')
      await onNoteAdded()
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="notes-panel">
      <h3>Notes — {patient.firstName} {patient.lastName}</h3>

      {notes.length === 0 ? (
        <p className="notes-empty">Aucune note pour ce patient.</p>
      ) : (
        <ul className="notes-list">
          {notes.map((note) => (
            <li key={note.id} className="note-item">
              <pre className="note-content">{note.content}</pre>
            </li>
          ))}
        </ul>
      )}

      <form onSubmit={handleSubmit} className="note-form">
        <label className="note-label">Ajouter une note</label>
        <textarea
          className="note-textarea"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          rows={4}
          placeholder="Observations de la visite…"
        />
        {error && <span className="field-error">{error}</span>}
        <button type="submit" className="btn-primary" disabled={submitting}>
          {submitting ? 'Enregistrement…' : 'Enregistrer la note'}
        </button>
      </form>
    </div>
  )
}
