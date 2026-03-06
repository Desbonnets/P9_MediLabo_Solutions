export default function UserList({ users, onEdit, onDelete }) {
  if (users.length === 0) {
    return <p>Aucun utilisateur.</p>
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
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {users.map((u) => (
          <tr key={u.id}>
            <td>{u.lastName}</td>
            <td>{u.firstName}</td>
            <td>{u.birthDate}</td>
            <td>{u.gender}</td>
            <td>{u.address}</td>
            <td>{u.phone}</td>
            <td>
              <button onClick={() => onEdit(u)}>Modifier</button>
              <button onClick={() => onDelete(u.id)} className="btn-danger">Supprimer</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
