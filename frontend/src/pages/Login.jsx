import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import styles from './Login.module.css';
import logo from '../assets/logo.png';

function Login() {
    function search(formData) {
        const username = formData.get("username");
        const password = formData.get("password");
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
          <form className={styles.input} action={search}>
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
        </div>
      </div>
    </div>
  );
}

export default Login;