const RISK_LABELS = {
  None:       { label: 'Aucun',              className: 'risk-none' },
  Borderline: { label: 'Risque limité',      className: 'risk-borderline' },
  InDanger:   { label: 'En danger',          className: 'risk-danger' },
  EarlyOnset: { label: 'Apparition précoce', className: 'risk-early' },
}

export default function UserList({ users, risks, selectedPatientId, onEdit, onDelete, onShowNotes }) {
  if (users.length === 0) {
    return <p>Aucun patient.</p>
  }

  return (
    <table className="user-table">
      <thead>
        <tr>
          <th>Nom</th>
          <th>Prénom</th>
          <th>Naissance</th>
          <th>Genre</th>
          <th>Adresse</th>
          <th>Téléphone</th>
          <th>Risque diabète</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {users.map((u) => {
          const riskKey = risks[u.id]
          const riskInfo = RISK_LABELS[riskKey]
          return (
            <tr key={u.id} className={selectedPatientId === u.id ? 'row-selected' : ''}>
              <td>{u.lastName}</td>
              <td>{u.firstName}</td>
              <td>{u.birthDate}</td>
              <td>{u.gender}</td>
              <td>{u.address}</td>
              <td>{u.phone}</td>
              <td>
                {riskInfo
                  ? <span className={`risk-badge ${riskInfo.className}`}>{riskInfo.label}</span>
                  : <span className="risk-badge risk-loading">—</span>
                }
              </td>
              <td>
                <button onClick={() => onShowNotes(u)} className="btn-notes">
                  {selectedPatientId === u.id ? 'Masquer notes' : 'Notes'}
                </button>
                <button onClick={() => onEdit(u)}>Modifier</button>
                <button onClick={() => onDelete(u.id)} className="btn-danger">Supprimer</button>
              </td>
            </tr>
          )
        })}
      </tbody>
    </table>
  )
}
