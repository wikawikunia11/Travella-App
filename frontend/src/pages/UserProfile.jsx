import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

function UserProfile() {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/users/${id}`)
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch user");
        return response.json();})
      .then(data => {setUser(data); setLoading(false);})
      .catch(err => {setError(err.message); setLoading(false);});
  }, [id]); // 'hook' starts when id changed

  if (loading) return <p>Loading...</p>;
if (error) return <p>Error: {error}</p>;
if (!user) return <p>No user data</p>;

  return (
    <div>
      <h1>Profile {user.id}</h1>
      <p>Name: {user.name}</p>
      <p>Nickname: {user.nickname}</p>
      <p>Email: {user.email}</p>
    </div>
  );
}

export default UserProfile;