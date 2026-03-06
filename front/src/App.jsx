import { useState, useEffect, useCallback } from 'react'
import { getUsers, createUser, updateUser, deleteUser } from './api/userApi'
import UserList from './components/UserList'
import UserForm from './components/UserForm'
import './App.css'

export default function App() {
  const [users, setUsers] = useState([])
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editingUser, setEditingUser] = useState(null)

  const loadUsers = useCallback(async () => {
    try {
      const data = await getUsers()
      setUsers(data)
      setError(null)
    } catch (e) {
      setError(e.message)
    }
  }, [])

  useEffect(() => {
    loadUsers()
  }, [loadUsers])

  function handleCreate() {
    setEditingUser(null)
    setShowForm(true)
  }

  function handleEdit(user) {
    setEditingUser(user)
    setShowForm(true)
  }

  async function handleDelete(id) {
    if (!confirm('Supprimer cet utilisateur ?')) return
    try {
      await deleteUser(id)
      await loadUsers()
    } catch (e) {
      setError(e.message)
    }
  }

  async function handleSubmit(formData) {
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
      setError(e.message)
    }
  }

  function handleCancel() {
    setShowForm(false)
    setEditingUser(null)
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
          />
        ) : (
          <>
            <button className="btn-primary" onClick={handleCreate}>
              + Nouvel utilisateur
            </button>
            <UserList users={users} onEdit={handleEdit} onDelete={handleDelete} />
          </>
        )}
      </main>
    </div>
  )
}
