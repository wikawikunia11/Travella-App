import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';

function UserEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState({
    nickname: '',
    name: '',
    surname: '',
    email: '',
    biography: '',
    avatarUrl: ''
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const defaultAvatar = "/vite.svg";

  useEffect(() => {
    fetch(`http://localhost:8080/api/users/${id}`)
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch user");
        return response.json();
      })
      .then(data => { setUser(data); setLoading(false); })
      .catch(err => { setError(err.message); setLoading(false); });
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    fetch(`http://localhost:8080/api/users/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user)
    })
      .then(response => {
        if (!response.ok) throw new Error("Failed to save user");
        return response.json();
      })
      .then(() => {
        navigate(`/profile/${id}`);
      })
      .catch(err => setError(err.message));
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "20px", padding: "20px" }}>
      <div style={{ display: "flex", alignItems: "center", gap: "15px" }}>
        <img
          src={user.avatarUrl || defaultAvatar}
          alt="profile"
          style={{ width: "80px", height: "80px", borderRadius: "50%" }}
        />
        <input
          type="text"
          name="nickname"
          value={user.nickname}
          onChange={handleChange}
          style={{ fontSize: "1.5rem" }}
        />
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: "10px", textAlign: "center" }}>
        <input
          type="text"
          name="name"
          value={user.name}
          onChange={handleChange}
          placeholder="First name"
        />
        <input
          type="text"
          name="surname"
          value={user.surname}
          onChange={handleChange}
          placeholder="Last name"
        />
        <input
          type="email"
          name="email"
          value={user.email}
          onChange={handleChange}
          placeholder="Email"
        />
        <textarea
          name="biography"
          value={user.biography}
          onChange={handleChange}
          placeholder="Biography"
          rows={4}
        />
        <input
          type="text"
          name="avatarUrl"
          value={user.avatarUrl}
          onChange={handleChange}
          placeholder="Avatar URL"
        />
      </div>

      <div style={{ display: "flex", gap: "5px" }}>
        <Link to={`/profile/${id}`}> <button>Cancel</button> </Link>
        <button onClick={handleSave}>Save</button>
      </div>
    </div>
  );
}

export default UserEdit;