import { useState, useEffect } from 'react'

const EMPTY = {
  firstName: '',
  lastName: '',
  birthDate: '',
  gender: '',
  address: '',
  phone: '',
}

function validate(form) {
  const errors = {}
  if (!form.firstName.trim()) errors.firstName = 'Le prénom est obligatoire'
  if (!form.lastName.trim()) errors.lastName = 'Le nom est obligatoire'
  if (!form.birthDate) errors.birthDate = 'La date de naissance est obligatoire'
  if (!form.gender) errors.gender = 'Le genre est obligatoire'
  return errors
}

export default function UserForm({ initial, onSubmit, onCancel, apiErrors = {} }) {
  const [form, setForm] = useState(EMPTY)
  const [errors, setErrors] = useState({})

  useEffect(() => {
    setForm(initial
      ? {
          firstName: initial.firstName ?? '',
          lastName: initial.lastName ?? '',
          birthDate: initial.birthDate ?? '',
          gender: initial.gender ?? '',
          address: initial.address ?? '',
          phone: initial.phone ?? '',
        }
      : EMPTY
    )
    setErrors({})
  }, [initial])

  // Les erreurs API sont écrasées par les erreurs locales si le même champ est en erreur
  const displayErrors = { ...apiErrors, ...errors }

  function handleChange(e) {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
    setErrors((prev) => { const next = { ...prev }; delete next[name]; return next })
  }

  function handleSubmit(e) {
    e.preventDefault()
    const clientErrors = validate(form)
    if (Object.keys(clientErrors).length > 0) {
      setErrors(clientErrors)
      return
    }
    onSubmit(form)
  }

  return (
    <form onSubmit={handleSubmit} className="user-form" noValidate>
      <h2>{initial ? 'Modifier' : 'Créer'} un patient</h2>

      <div className="form-row">
        <label>Prénom *</label>
        <input name="firstName" value={form.firstName} onChange={handleChange} />
        {displayErrors.firstName && <span className="field-error">{displayErrors.firstName}</span>}
      </div>

      <div className="form-row">
        <label>Nom *</label>
        <input name="lastName" value={form.lastName} onChange={handleChange} />
        {displayErrors.lastName && <span className="field-error">{displayErrors.lastName}</span>}
      </div>

      <div className="form-row">
        <label>Date de naissance *</label>
        <input type="date" name="birthDate" value={form.birthDate} onChange={handleChange} />
        {displayErrors.birthDate && <span className="field-error">{displayErrors.birthDate}</span>}
      </div>

      <div className="form-row">
        <label>Genre *</label>
        <select name="gender" value={form.gender} onChange={handleChange}>
          <option value="">--</option>
          <option value="M">Homme</option>
          <option value="F">Femme</option>
        </select>
        {displayErrors.gender && <span className="field-error">{displayErrors.gender}</span>}
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
