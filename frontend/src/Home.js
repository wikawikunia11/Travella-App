import React from 'react';
import { Link } from 'react-router-dom';

function Home({ message }) {
  return (
    <div>
      <header>Welcome to Travel-Fun</header>
      <Link to="/profile/1">
        <button>Pokaż profil</button>
      </Link>
    </div>
  );
}

export default Home;