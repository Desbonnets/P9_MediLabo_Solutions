const BASE = '/api/assess'

export async function getPatientRisk(patientId) {
  const res = await fetch(`${BASE}/${patientId}`)
  if (!res.ok) return null
  const data = await res.json()
  return data.risk
}
