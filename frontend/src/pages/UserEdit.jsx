import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useUser } from '../UserContext';

function UserEdit() {
  const { username } = useParams();
  const navigate = useNavigate();
  const { token, user: loggedInUser, refresh, setRefresh} = useUser();
  const [user, setUser] = useState({
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
    if (loggedInUser && loggedInUser.username !== username) {
      alert("Not authorized to perform this action.");
      navigate(`/profile/${username}`);
    }
  }, [loggedInUser, username, navigate]);

  useEffect(() => {
    if (!token) return;

    fetch(`http://localhost:8080/api/users/${username}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })
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
  }, [username, token, refresh]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    fetch(`http://localhost:8080/api/users/${username}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(user)
    })
      .then(response => {
        if (!response.ok) {
          if (response.status === 403) throw new Error("Forbidden");
          throw new Error("Failed to save user");
        }
        return response.json();
      })
      .then(() => {
        setRefresh(true);
        navigate(`/profile/${username}`);
      })
      .catch(err => setError(err.message));
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-start", gap: "20px", padding: "20px", width: '100%' }}>
      <p>{user.id}</p>
      <div style={{ display: "flex", alignItems: "center", gap: "15px" }}>
        <img
          src={user.profilePic || defaultAvatar}
          alt="profile"
          style={{ width: "80px", height: "80px", borderRadius: "50%",
            objectFit: 'scale-down',
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)'}}
        />
        <p style={{ fontSize: "1.5rem" }}>{user.username}</p>
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: "10px", textAlign: "center", width: '100%' }}>
        <input
          type="text"
          name="name"
          value={user.name}
          onChange={handleChange}
          placeholder="First name"
          style={{width: '30%', fontSize: '1rem', border: '1px solid #aeb1b6', borderRadius: '10px'}}
        />
        <input
          type="text"
          name="surname"
          value={user.surname}
          onChange={handleChange}
          placeholder="Last name"
          style={{width: '30%', fontSize: '1rem', border: '1px solid #aeb1b6', borderRadius: '10px'}}
        />
        <textarea
          name="biography"
          value={user.biography}
          onChange={handleChange}
          placeholder="Biography"
          style={{width: '80%', fontSize: '1rem', fontFamily: 'Arial'}}
          rows={4}
        />
        <textarea
          name="profilePic"
          value={user.profilePic}
          onChange={handleChange}
          placeholder="Profile picture URL"
          style={{width: '80%', fontSize: '1rem', fontFamily: 'Arial'}}
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
