const BASE = '/api/users'

async function handleResponse(res) {
  if (res.ok) return res.status === 204 ? null : res.json()

  let body = null
  try { body = await res.json() } catch { /* pas de corps JSON */ }

  if (res.status === 400 && body && typeof body === 'object' && !body.message) {
    const err = new Error('Données invalides')
    err.fields = body
    throw err
  }

  throw new Error(body?.message || `Erreur ${res.status}`)
}

export async function getUsers() {
  const res = await fetch(BASE)
  return handleResponse(res)
}

export async function getUserById(id) {
  const res = await fetch(`${BASE}/${id}`)
  return handleResponse(res)
}

export async function createUser(user) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
  })
  return handleResponse(res)
}

export async function updateUser(id, user) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
  })
  return handleResponse(res)
}

export async function deleteUser(id) {
  const res = await fetch(`${BASE}/${id}`, { method: 'DELETE' })
  return handleResponse(res)
}
