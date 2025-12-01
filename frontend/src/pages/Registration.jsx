import React from 'react';
import { Link } from 'react-router-dom';
import styles from './Registration.module.css';
import logo from '../assets/logo.png';

function Login() {
    function search(formData) {
        const username = formData.get("username");
        const password = formData.get("password");
        const name = formData.get("name");
        const surname = formData.get("surname");
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
          <header className={styles.text_title}>Start your journey with us 🚀</header>
          <form className={styles.input} action={search}>
            <p className={styles.above_input}>Name</p>
            <input name="name" className={styles.input_box} placeholder="Your name"/>
            <p className={styles.above_input}>Surname</p>
            <input name="surname" className={styles.input_box} placeholder="Your surname" />
            <p className={styles.above_input}>Create username</p>
            <input name="username" className={styles.input_box} placeholder="Username"/>
            <p className={styles.above_input}>Create password</p>
            <input name="password" className={styles.input_box} placeholder="Password"/>
            <button type="submit" className={styles.button_box}>
              <p className={styles.button_text}>Register</p>
            </button>
          </form>
          <div className={styles.register}>
            <p className={styles.register_text}>Already have an account?
              <a href="/login" className={styles.register_link}>
              Log in here
            </a></p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;