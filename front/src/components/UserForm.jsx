import { useState, useEffect } from 'react'

const EMPTY = {
  firstName: '',
  lastName: '',
  birthDate: '',
  gender: '',
  address: '',
  phone: '',
}

export default function UserForm({ initial, onSubmit, onCancel }) {
  const [form, setForm] = useState(EMPTY)

  useEffect(() => {
    if (initial) {
      setForm({
        firstName: initial.firstName ?? '',
        lastName: initial.lastName ?? '',
        birthDate: initial.birthDate ?? '',
        gender: initial.gender ?? '',
        address: initial.address ?? '',
        phone: initial.phone ?? '',
      })
    } else {
      setForm(EMPTY)
    }
  }, [initial])

  function handleChange(e) {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }))
  }

  function handleSubmit(e) {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <form onSubmit={handleSubmit} className="user-form">
      <h2>{initial ? 'Modifier' : 'Créer'} un utilisateur</h2>

      <div className="form-row">
        <label>Prénom</label>
        <input name="firstName" value={form.firstName} onChange={handleChange} required />
      </div>
      <div className="form-row">
        <label>Nom</label>
        <input name="lastName" value={form.lastName} onChange={handleChange} required />
      </div>
      <div className="form-row">
        <label>Date de naissance</label>
        <input type="date" name="birthDate" value={form.birthDate} onChange={handleChange} />
      </div>
      <div className="form-row">
        <label>Genre</label>
        <select name="gender" value={form.gender} onChange={handleChange}>
          <option value="">--</option>
          <option value="M">Homme</option>
          <option value="F">Femme</option>
        </select>
      </div>
      <div className="form-row">
        <label>Adresse</label>
        <input name="address" value={form.address} onChange={handleChange} />
      </div>
      <div className="form-row">
        <label>Téléphone</label>
        <input name="phone" value={form.phone} onChange={handleChange} />
      </div>

      <div className="form-actions">
        <button type="submit">{initial ? 'Enregistrer' : 'Créer'}</button>
        <button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}
