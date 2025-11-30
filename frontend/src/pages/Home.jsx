import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

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
    <div>
      <header>Welcome to Travel-Fun</header>
      <Link to="/profile/1"><button>Show profile</button></Link>
      <Link to="/map"><button>View map</button></Link>
      {message || 'Loading message from backend...'}
    </div>
  );
}

export default Home;