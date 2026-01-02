import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { useUser } from '../UserContext';
import styles from './UserProfile.module.css';

function Profile() {
  const { username } = useParams();
  const navigate = useNavigate();
  const { user, login, logout } = useUser();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const defaultAvatar = "/vite.svg";

  useEffect(() => {
    fetch(`http://localhost:8080/api/users/username/${username}`)
    .then(async response => {
      if (!response.ok) throw new Error("Failed to fetch user");
      const text = await response.text(); // najpierw pobierz tekst
      return text ? JSON.parse(text) : {}; // jeśli pusty tekst, zwróć pusty obiekt
    })
    .then(data => {
      setProfile(data);
      setLoading(false);
      console.log(user);
      if (user !== null && user.username == username) {
        const userData = { id: data.id, username: username }; // Simplified based on your code
        login(userData);
        console.log(user);
      }
    })
    .catch(err => { setError(err.message); setLoading(false); });
  }, [username]);

  function handleLogout() {
    logout();
    navigate('/login');
  }

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;
  if (!profile) return <p>No user data</p>;

  return (
    <div className={styles.main}>
        <img
          src={profile.profilePic || defaultAvatar}
          alt="profile"
          className={styles.img}
        />
        <h1>{profile.name} {profile.surname}</h1>
        <h2>{profile.username}</h2>
        <h2>Friends: 10</h2>
        <h4>{profile.biography}</h4>
    </div>
  );
}

export default Profile;