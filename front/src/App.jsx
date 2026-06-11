import { useState, useEffect, useCallback } from 'react'
import { getUsers, createUser, updateUser, deleteUser } from './api/userApi'
import { getNotesByPatient } from './api/notesApi'
import { getPatientRisk } from './api/riskApi'
import UserList from './components/UserList'
import UserForm from './components/UserForm'
import NotesPanel from './components/NotesPanel'
import './App.css'

export default function App() {
  const [users, setUsers] = useState([])
  const [risks, setRisks] = useState({})
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editingUser, setEditingUser] = useState(null)
  const [formApiErrors, setFormApiErrors] = useState({})
  const [selectedPatient, setSelectedPatient] = useState(null)
  const [notes, setNotes] = useState([])

  const loadUsers = useCallback(async () => {
    try {
      const data = await getUsers()
      setUsers(data)
      setError(null)
      loadRisks(data)
    } catch (e) {
      setError(e.message)
    }
  }, [])

  async function loadRisks(patients) {
    const entries = await Promise.all(
      patients.map(async (p) => {
        const risk = await getPatientRisk(p.id)
        return [p.id, risk]
      })
    )
    setRisks(Object.fromEntries(entries))
  }

  useEffect(() => {
    loadUsers()
  }, [loadUsers])

  async function handleShowNotes(patient) {
    if (selectedPatient?.id === patient.id) {
      setSelectedPatient(null)
      setNotes([])
      return
    }
    setSelectedPatient(patient)
    await refreshNotes(patient.id)
  }

  async function refreshNotes(patId) {
    try {
      const [data, risk] = await Promise.all([
        getNotesByPatient(patId),
        getPatientRisk(patId),
      ])
      setNotes(data)
      setRisks(prev => ({ ...prev, [patId]: risk }))
    } catch (e) {
      setError(e.message)
    }
  }

  function handleCreate() {
    setEditingUser(null)
    setFormApiErrors({})
    setShowForm(true)
  }

  function handleEdit(user) {
    setEditingUser(user)
    setFormApiErrors({})
    setShowForm(true)
  }

  async function handleDelete(id) {
    if (!confirm('Supprimer ce patient ?')) return
    try {
      await deleteUser(id)
      if (selectedPatient?.id === id) {
        setSelectedPatient(null)
        setNotes([])
      }
      await loadUsers()
    } catch (e) {
      setError(e.message)
    }
  }

  async function handleSubmit(formData) {
    setFormApiErrors({})
    setError(null)
    try {
      if (editingUser) {
        await updateUser(editingUser.id, formData)
      } else {
        await createUser(formData)
      }
      setShowForm(false)
      setEditingUser(null)
      await loadUsers()
    } catch (e) {
      if (e.fields) {
        setFormApiErrors(e.fields)
      } else {
        setError(e.message)
      }
    }
  }

  function handleCancel() {
    setShowForm(false)
    setEditingUser(null)
    setFormApiErrors({})
    setError(null)
  }

  return (
    <div className="app">
      <header>
        <h1>MediLabo Solutions - Patients</h1>
      </header>

      <main>
        {error && <div className="error">{error}</div>}

        {showForm ? (
          <UserForm
            initial={editingUser}
            onSubmit={handleSubmit}
            onCancel={handleCancel}
            apiErrors={formApiErrors}
          />
        ) : (
          <>
            <button className="btn-primary" onClick={handleCreate}>
              + Nouveau patient
            </button>
            <UserList
              users={users}
              risks={risks}
              selectedPatientId={selectedPatient?.id}
              onEdit={handleEdit}
              onDelete={handleDelete}
              onShowNotes={handleShowNotes}
            />
            {selectedPatient && (
              <NotesPanel
                patient={selectedPatient}
                notes={notes}
                onNoteAdded={() => refreshNotes(selectedPatient.id)}
              />
            )}
          </>
        )}
      </main>
    </div>
  )
}
