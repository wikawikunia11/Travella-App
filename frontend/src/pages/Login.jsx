import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useUser } from '../UserContext';
import styles from './Login.module.css';
import logo from '../assets/logo.png'
import { IoEyeOutline } from "react-icons/io5";
import { IoEyeOffOutline } from "react-icons/io5";

function Login() {
   const [message, setMessage] = useState('');
   const [type, setType] = useState('password');
   const [off, setOff] = useState(true);
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

    const handleToggle = () => {
        setOff(!off);
        if (type==='password'){
            setType('text')
        } else {
            setType('password')
        }
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
              <div className={styles.input_box} style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                  <input name="password" placeholder="Password" type={type}
                         autoComplete="off"/>
                  <span className="flex justify-around items-center" onClick={handleToggle}>
                    {off ? (<IoEyeOffOutline onClick={handleToggle} />) : (<IoEyeOutline onClick={handleToggle} />)}
                  </span>
              </div>
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