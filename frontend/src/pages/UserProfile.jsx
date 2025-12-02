import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';

function UserProfile() {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const defaultAvatar = "/vite.svg";

  useEffect(() => {
    fetch(`http://localhost:8080/api/users/${id}`)
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch user");
        return response.json();})
      .then(data => {setUser(data); setLoading(false);})
      .catch(err => {setError(err.message); setLoading(false);});
  }, [id]);

  if (loading) return <p>Loading...</p>;
if (error) return <p>Error: {error}</p>;
if (!user) return <p>No user data</p>;

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "20px", padding: "20px" }}>
  <div style={{ display: "flex", alignItems: "center", gap: "15px" }}>
    <img
      src={user.avatarUrl || defaultAvatar}
      alt="profile"
      style={{ width: "80px", height: "80px", borderRadius: "50%" }}
    />
    <h1 style={{ margin: 0 }}>{user.nickname}</h1>
  </div>

  <div style={{ textAlign: "center" }}>
    <p style={{ margin: "3px 0" }}>{user.name} {user.surname}</p>
    {user.biography && (<p style={{ margin: "3px 0" }}><strong>Bio:</strong> {user.biography}</p>)}
  </div>

  <div style={{ display: "flex", gap: "5px" }}>
    <Link to="/"> <button>Main page</button> </Link>
    <Link to={`/profile/${id}/edit`}> <button>Edit profile</button> </Link>
    <button>Posts</button>
  </div>
</div>
  );
}

export default UserProfile;