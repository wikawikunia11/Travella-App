import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import MapView from './MapView';
import styles from './Registration.module.css';
import { useUser } from '../UserContext'; // import contextu z tokenem

function UserPosts() {
  const { username } = useParams();
  const { token } = useUser();
  const [posts, setPosts] = useState([]);
  const [selectedMarker, setSelectedMarker] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/posts/user/${username}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch posts");
        return response.json();
      })
      .then(data => {
        setPosts(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, [username, token]);

  const fetchImageWithToken = async (url) => {
    const res = await fetch(url, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) return null;
    const blob = await res.blob();
    return URL.createObjectURL(blob);
  };

  const fetchImages = async (postId) => {
    try {
      const res = await fetch(`http://localhost:8080/api/posts/${postId}/images`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) return [];
      const paths = await res.json();
      const urls = await Promise.all(paths.map(p => fetchImageWithToken(`http://localhost:8080${p}`)));
      return urls.filter(u => u !== null);
    } catch (err) {
      console.error(err);
      return [];
    }
  };



  const handleMarkerClick = async (post) => {
    const images = await fetchImages(post.id);
    setSelectedMarker({ ...post, images });
  };

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
          id: post.idPost,
          name: post.caption,
          description: post.description,
          position: [post.latitude, post.longitude]
        }))}
        markerClicked={handleMarkerClick}
      />
      {selectedMarker ? (
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(2, 1fr)",
            gap: "10px",
            marginTop: "10px"
          }}
        >
          {selectedMarker.images.map((url, index) => (
            <img
              key={index}
              src={url}
              alt={`Image ${index + 1}`}
              style={{
                width: "100%",
                height: "auto",
                borderRadius: "8px",
                objectFit: "cover"
              }}
            />
          ))}
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
