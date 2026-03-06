const BASE = '/api/users'

export async function getUsers() {
  const res = await fetch(BASE)
  if (!res.ok) throw new Error('Erreur lors du chargement des utilisateurs')
  return res.json()
}

export async function getUserById(id) {
  const res = await fetch(`${BASE}/${id}`)
  if (!res.ok) throw new Error('Utilisateur introuvable')
  return res.json()
}

export async function createUser(user) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
  })
  if (!res.ok) throw new Error('Erreur lors de la création')
  return res.json()
}

export async function updateUser(id, user) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
  })
  if (!res.ok) throw new Error('Erreur lors de la mise à jour')
  return res.json()
}

export async function deleteUser(id) {
  const res = await fetch(`${BASE}/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('Erreur lors de la suppression')
}
