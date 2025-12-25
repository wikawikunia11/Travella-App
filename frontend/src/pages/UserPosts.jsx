import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import PostMarker from './PostMarker';
import json_posts from '../assets/json_posts';
import MapView from './MapView';
import styles from './Registration.module.css';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png",
});

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
    <div style={{width: "100%", height: "100%", borderRadius: "10px", backgroundColor: "#dce7daff" }}>
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "20px", padding: "20px"}}>
            <h2>The posts of 🦋{username}🦋</h2>
            <MapView width="800px" height="400px" markerData={posts} markerClicked={setSelectedMarker}/>
            {selectedMarker ? (
                <div>
                    <h3>{selectedMarker.name}</h3>
                    <p>{selectedMarker.description}</p>
                </div>
            ) : (
                <p>Click on a marker to see details.</p>
            )}
            <Link to={`/addpost/${username}`} style={{width: "100%"}}><button className={styles.button_box} style={{backgroundColor: "#225219ff"}}>
                <p className={styles.button_text}>Add new post</p>
            </button></Link>
        </div>
    </div>
  );
}

export default UserPosts;
