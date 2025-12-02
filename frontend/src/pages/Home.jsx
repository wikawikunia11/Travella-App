import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import MapView from './MapView';

function Home() {
  const [message, setMessage] = useState('');
  useEffect(() => {
    console.log('Fetching message from backend...');
    fetch('http://localhost:8080/api/message')
      .then(response => response.text())
      .then(data => {setMessage(data); console.log(data)})
      .catch(error => console.error('Error fetching message:', error));
  }, [])

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "20px", padding: "20px" }}>
      <header style={{ fontSize: "2em", marginBottom: "20px" }}>Welcome to Travel-Fun</header>

      <div style={{ display: "flex", gap: "10px" }}>
        <Link to="/profile/1"><button>Show profile</button></Link>
        <button>Log out</button>
      </div>

      <p>{message || 'Loading message from backend...'}</p>

      <MapView width="800px" height="400px" markerPosition={[50.0647, 19.9450]} />
    </div>
  );
}

export default Home;