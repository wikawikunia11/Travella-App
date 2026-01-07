import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { useUser } from '../UserContext';
import styles from './UserSidebar.module.css';
import { CgProfile } from "react-icons/cg";
import { IoMdPin } from "react-icons/io";
import { LiaUserFriendsSolid } from "react-icons/lia";
import { CiEdit } from "react-icons/ci";
import { MdOutlinePostAdd } from "react-icons/md";
import { RiLogoutCircleLine } from "react-icons/ri";

function UserSidebar() {
  const { username } = useParams();
  const navigate = useNavigate();
  const { user, token, login, logout, refresh, setRefresh } = useUser();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const defaultAvatar = "/vite.svg";

  useEffect(() => {
  if (!token || token === "null") return;
  setRefresh(false);
  setLoading(true);
  fetch(`http://localhost:8080/api/users/${username}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  })
  .then(async response => {
    if (!response.ok) throw new Error("Failed to fetch user");
    const data = await response.json();
    setProfile(data);
    setLoading(false);
  })
  .catch(err => {
    setError(err.message);
    setLoading(false);
  });
}, [username, token, refresh]);

  function handleLogout() {
    logout();
    navigate('/login');
  }

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;
  if (!profile) return <p>No user data</p>;

  return (
    <div className={styles.main}>
        <div className={styles.user}>
          <img
            src={profile.profilePic || defaultAvatar}
            alt="profile"
            className={styles.img}
          />
          <h3>{profile.username}</h3>
        </div>
        <div className={styles.menu}>
          {user !== null && user.username !== username && (
            <div className={styles.menuItem}>
              <CgProfile className={styles.icon} />
              <button
                className={styles.button}
                onClick={() => navigate(`/profile/${user.username}`)}
              >
                Back to profile
              </button>
            </div>
          )}
          <div className={styles.menuItem}>
            <CgProfile className={styles.icon}/>
            <Link to={`/profile/${username}`}>
              <button className={styles.button}>Profile</button>
            </Link>
          </div>
          <div className={styles.menuItem}>
            <IoMdPin  className={styles.icon}/>
            <Link to={`/profile/${username}/posts`}>
              <button className={styles.button}>Posts</button>
            </Link>
          </div>
          <div className={styles.menuItem}>
            <LiaUserFriendsSolid className={styles.icon}/>
            <Link to={`/profile/${username}/friends`}>
              <button className={styles.button}>Friends</button>
            </Link>
          </div>
          {(user !== null && user.username === username) && (
            <div className={styles.menuItem}>
              <CiEdit className={styles.icon}/>
              <Link to={`/profile/${username}/edit`}>
                <button className={styles.button}>Edit profile</button>
              </Link>
            </div> )}
          {(user !== null && user.username === username) && (
            <div className={styles.menuItem}>
              <MdOutlinePostAdd className={styles.icon}/>
              <Link to={`/profile/${username}/addpost`}>
                <button className={styles.button}>Add post</button>
              </Link>
            </div>  )}
        </div>
        <div>
          {(user !== null && user.username === username) && (
            <button onClick={handleLogout} className={styles.logout}><RiLogoutCircleLine  className={styles.icon}/>Log out</button>)}
        </div>
    </div>
  );
}

export default UserSidebar;