import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

function Login() {
  return (
    <div>
      <h1>Log in</h1>
      <Link to="/profile/1">
        <button>Log in</button>
      </Link>
      <header>Don't have an account?</header>
      <a href="/register">
          <p>go to registration</p>
    </a>
    </div>
  );
}

export default Login;