import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { useUser } from '../UserContext';
import styles from './UserProfile.module.css';

function Profile() {
  const { username } = useParams();
  const navigate = useNavigate();
  const { user, token, login, logout } = useUser();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const defaultAvatar = "/vite.svg";

useEffect(() => {
  if (!token || token === "null" || !token.includes('.')) {
    console.log(token);
    return;
  }

  fetch(`http://localhost:8080/api/users/${username}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  })
  .then(async response => {
    if (!response.ok) throw new Error(`Błąd: ${response.status}`);
    return response.json();
  })
  .then(data => {
    setProfile(data);
    setLoading(false);
  })
  .catch(err => {
    setError(err.message);
    setLoading(false);
  });
}, [username, token]);

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
        <h1 className={styles.name}>
          {profile.name} {profile.surname}
        </h1>

        <p className={styles.username}>
          @{profile.username}
        </p>
      {profile.biography && <p className={styles.stats}>{profile.biography}</p>}
    </div>
  );
}

export default Profile;