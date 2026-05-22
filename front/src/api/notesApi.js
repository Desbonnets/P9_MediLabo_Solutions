const BASE = '/api/notes'

async function handleResponse(res) {
  if (res.ok) return res.status === 204 ? null : res.json()
  let body = null
  try { body = await res.json() } catch { /* pas de JSON */ }
  throw new Error(body?.message || `Erreur ${res.status}`)
}

export async function getNotesByPatient(patId) {
  const res = await fetch(`${BASE}/patient/${patId}`)
  return handleResponse(res)
}

export async function createNote(note) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(note),
  })
  return handleResponse(res)
}
