import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useUser } from '../UserContext';
import styles from './Login.module.css';
import logo from '../assets/logo.png';

function Login() {
   const [message, setMessage] = useState('');
   const navigate = useNavigate();
   const { login } = useUser();

    function handleLogin(e) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const username = formData.get("username");
        const password = formData.get("password");
        const data = {username: username, password: password};
        const requestOptions = {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(data)
        };
        fetch('http://localhost:8080/api/login', requestOptions)
          .then(response => {
            if (!response.ok) {
              setMessage("Wrong username or password - try again.")
              throw new Error(message);
            }
            return response.json();
          })
          .then(apiResponse => {
          const userData = {
              username: username
          };
          const tokenValue = apiResponse.token;
          login(userData, tokenValue);
          navigate(`/profile/${username}`);
      })
      .catch(error => console.error('Login error:', error));
    }

    return (
    <div className={styles.root}>
      <div className={styles.image}></div>
      <div className={styles.right_panel}>
        <div className={styles.app_info}>
          <img src={logo} alt="App Logo" className={styles.logo}/>
          <span className={styles.title}>Travella</span>
        </div>
        <div className={styles.form}>
          <header className={styles.text_title}>Welcome back!</header>
          <form className={styles.input} onSubmit={handleLogin}>
            <p className={styles.above_input}>Login</p>
            <input name="username" className={styles.input_box} placeholder="Username"/>
            <p className={styles.above_input}>Password</p>
            <input name="password" className={styles.input_box} placeholder="Password" type="password"/>
            <button type="submit" className={styles.button_box}>
              <p className={styles.button_text}>Log in</p>
            </button>
          </form>
          <div className={styles.register}>
            <p className={styles.register_text}>Don't have an account?
              <Link to="/register" className={styles.register_link}> Go to registration </Link>
            </p>
          </div>
          <div className={styles.register}>
            <p className={styles.register_text} style={{ color: '#d00000ff' }}>
              {message}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;