import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import styles from './Login.module.css';
import logo from '../assets/logo.png';

function Login() {
   const [message, setMessage] = useState('');

    function login(formData) {
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
            window.location.href = `/profile/${data.username}`;
            })
          .catch(error => console.error('', error));
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
          <form className={styles.input} action={login}>
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
              <a href="/register" className={styles.register_link}>
              Go to registration
            </a></p>
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