import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import MapView from './MapView';
import styles from './Registration.module.css';
import { useUser } from '../UserContext'; // import contextu z tokenem

function UserPosts() {
  const { username } = useParams();
  const { token } = useUser(); // pobranie tokena z kontekstu
  const [posts, setPosts] = useState([]); // tablica postów
  const [selectedMarker, setSelectedMarker] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/posts/user/${username}`, {
      headers: { 'Authorization': `Bearer ${token}` } // dodanie tokena
    })
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch posts");
        return response.json();
      })
      .then(data => {
        setPosts(data); // backend zwraca tablicę postów
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, [username, token]);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div style={{width: "100rem", height: "100rem", borderRadius: "10px", backgroundColor: "#dce7daff" }}>
      <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "20px", padding: "20px"}}>
        <h2>The posts of 🦋{username}🦋</h2>
        <MapView
          width="800px"
          height="400px"
          markerData={posts.map(post => ({
            name: post.caption,
            description: post.description,
            position: [post.latitude, post.longitude]
          }))}
          markerClicked={setSelectedMarker}
        />
        {selectedMarker ? (
          <div>
            <h3>{selectedMarker.name}</h3>
            <p>{selectedMarker.description}</p>
          </div>
        ) : (
          <p>Click on a marker to see details.</p>
        )}
        <Link to={`/profile/${username}/addpost`} style={{width: "100%"}}>
          <button className={styles.button_box} style={{backgroundColor: "#225219ff"}}>
            <p className={styles.button_text}>Add new post</p>
          </button>
        </Link>
      </div>
    </div>
  );
}

export default UserPosts;
