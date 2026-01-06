import React, {useState} from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styles from './Registration.module.css';
import logo from '../assets/logo.png';
import { useUser } from '../UserContext';
import { IoEyeOutline } from "react-icons/io5";
import { IoEyeOffOutline } from "react-icons/io5";

function Registration() {
  const { login } = useUser();
  const navigate = useNavigate();
  const [type, setType] = useState('password');
  const [off, setOff] = useState(true);

  const [message, setMessage] = useState('');
    function register(e) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const userData = {
          username: formData.get("username"),
          password: formData.get("password"),
          name: formData.get("name"),
          surname: formData.get("surname")
        };
        const requestOptions = {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(userData)
        };
        fetch('http://localhost:8080/api/users', requestOptions)
          .then(response => {
            if (!response.ok) {
              setMessage("Choose another username.")
              throw new Error(message);
            }
            return response.json();
          })
          .then(data => {
              localStorage.setItem('token', data.token);
              login(data.user, data.token);
              navigate(`/profile/${data.user.username}`);
            })
            .catch(error => {
              console.error('Error:', error);
              setMessage("Choose another username or check connection.");
            });
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
          <header className={styles.text_title}>Start your journey with us 🚀</header>
          <form className={styles.input} onSubmit={register}>
            <p className={styles.above_input}>Name</p>
            <input name="name" className={styles.input_box} placeholder="Your name"/>
            <p className={styles.above_input}>Surname</p>
            <input name="surname" className={styles.input_box} placeholder="Your surname" />
            <p className={styles.above_input}>Create username</p>
            <input name="username" className={styles.input_box} placeholder="Username"/>
            <p className={styles.above_input}>Create password</p>
          <div className={styles.input_box} style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
              <input name="password" placeholder="Password" type={type}
                     autoComplete="current-password"/>
              <span className="flex justify-around items-center" onClick={handleToggle}>
                {off ? (<IoEyeOffOutline onClick={handleToggle} />) : (<IoEyeOutline onClick={handleToggle} />)}
              </span>
          </div>
            <button type="submit" className={styles.button_box}>
              <p className={styles.button_text}>Register</p>
            </button>
          </form>
          <div className={styles.register}>
            <p className={styles.register_text}>Already have an account?
              <Link to="/login" className={styles.register_link}> Log in here </Link>
            </p>
            <p className={styles.register_text}>
              {message}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Registration;