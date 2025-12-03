import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';

function UserEdit() {
  const { username } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState({
    username: '',
    name: '',
    surname: '',
    biography: '',
    profilePic: ''
  });
  const [userID, setUserID] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const defaultAvatar = "/vite.svg";

  useEffect(() => {
    fetch(`http://localhost:8080/api/users/username/${username}`)
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch user");
        return response.json();
      })
      .then(data => {
        setUser({
          username: data.username || '',
          name: data.name || '',
          surname: data.surname || '',
          biography: data.biography || '',
          profilePic: data.profilePic || ''
        });
        setUserID(data.id);
        setLoading(false);
      })
      .catch(err => { setError(err.message); setLoading(false); });
  }, [username]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    fetch(`http://localhost:8080/api/users/${userID}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user)
    })
      .then(response => {
        if (!response.ok) throw new Error("Failed to save user");
        return response.json();
      })
      .then(() => {
        navigate(`/profile/${username}`);
      })
      .catch(err => setError(err.message));
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "20px", padding: "20px" }}>
      <p>{user.id}</p>
      <div style={{ display: "flex", alignItems: "center", gap: "15px" }}>
        <img
          src={user.profilePic || defaultAvatar}
          alt="profile"
          style={{ width: "80px", height: "80px", borderRadius: "50%" }}
        />
        <input
          type="text"
          name="username"
          value={user.username}
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
        <textarea
          name="biography"
          value={user.biography}
          onChange={handleChange}
          placeholder="Biography"
          rows={4}
        />
        <input
          type="text"
          name="profilePic"
          value={user.profilePic}
          onChange={handleChange}
          placeholder="Profile picture URL"
        />
      </div>

      <div style={{ display: "flex", gap: "5px" }}>
        <Link to={`/profile/${username}`}><button>Cancel</button></Link>
        <button onClick={handleSave}>Save</button>
      </div>
    </div>
  );
}

export default UserEdit;
