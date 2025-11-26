import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

function Registration() {
  return (
    <div>
      <header>Register</header>
      <Link to="/login">
        <button>Register</button>
      </Link>
    </div>
  );
}

export default Registration;