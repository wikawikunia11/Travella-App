import './App.css';
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import UserProfile from './UserProfile';
import Home from './Home';

function App() {
  /*
  const [message, setMessage] = useState('');
  useEffect(() => {
    console.log('Fetching message from backend...');
    fetch('http://localhost:8080/api/message')
      .then(response => response.text())
      .then(data => {setMessage(data); console.log(data)})
      .catch(error => console.error('Error fetching message:', error));
  }, [])
  return (
    <div className="App">
      <header className='App-header'>
        {message || 'Loading message from backend...'}
      </header>
    </div>
  );
  */

  // return (
  //   <div className="App">
  //     <button onClick={toggleProfile}>
  //       {showProfile ? 'Ukryj profil' : 'Pokaż profil'}
  //     </button>

  //     {showProfile && <UserProfile />}
  //   </div>
  // );

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/profile/:id" element={<UserProfile />} />
      </Routes>
    </Router>
  );
}

export default App;

