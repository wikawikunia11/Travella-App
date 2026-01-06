import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import json_posts from '../assets/json_posts';
import MapView from './MapView';
import styles from './UserProfile.module.css';
import { PiSparkle } from "react-icons/pi";

function UserPosts() {
  const { username } = useParams();
  const navigate = useNavigate();
  const [posts, setPosts] = useState({
    name: '',
    position: [52.2297, 21.0122]
  });
  const [selectedMarker, setSelectedMarker] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/posts/username/${username}`)
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch user");
        return response.json();
      })
      .then(data => {
        setPosts({
          name: data.name || '',
          position: data.position || [52.2297, 21.0122]
        });
        setLoading(false);
      })
      .catch(err => { setLoading(false); setPosts(json_posts); console.log(posts);});
  }, [username]);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div style={{display: 'flex',
            flexDirection: 'column',
            gap: '10px',
            width: '100%',
            backgroundColor: '#ffffff',
            margin: '10px',
            borderRadius: '24px',
            boxShadow: '0 10px 30px rgba(0, 0, 0, 0.08)',
            border: '1px solid rgba(0, 0, 0, 0.05)'}}>
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "20px", padding: "20px"}}>
            <p style={{fontSize: "20px"}}>The posts of <PiSparkle /><b>{username}</b><PiSparkle /></p>
            <MapView width="80%" height="400px" markerData={posts} markerClicked={setSelectedMarker}/>
            {selectedMarker ? (
                <div>
                    <h3>{selectedMarker.name}</h3>
                    <p>{selectedMarker.description}</p>
                </div>
            ) : (
                <p>Click on a marker to see details.</p>
            )}
            <Link to={`/profile/${username}/addpost`} style={{width: "80%"}}><button className={styles.button_box} style={{backgroundColor: "#225219ff"}}>
                <p
                    className={styles.button_text}
                >Add new post</p>
            </button></Link>
        </div>
    </div>
  );
}

export default UserPosts;
