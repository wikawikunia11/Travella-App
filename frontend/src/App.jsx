import './App.css';
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { UserProvider } from './UserContext';
import Login from './pages/Login';
import Registration from './pages/Registration';
import UserEdit from './pages/UserEdit';
import UserPosts from './pages/UserPosts';
import NewPostForm from './pages/NewPostForm';
import UserPage from './pages/UserPage';
import UserProfile from './pages/UserProfile';
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function App() {
  return (
    <>
    <ToastContainer
        position="bottom-right"
        autoClose={2500}
        hideProgressBar={false}
        closeOnClick
        pauseOnHover
        draggable
        theme="colored"
      />
    <UserProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Registration />} />
          <Route path="/profile/:username" element={<UserPage />} >
            <Route path="/profile/:username/" element={<UserProfile />} />
            <Route path="/profile/:username/edit" element={<UserEdit />} />
            <Route path="/profile/:username/posts" element={<UserPosts />} />
            <Route path="/profile/:username/addpost" element={<NewPostForm />} />
          </Route>
        </Routes>
      </Router>
    </UserProvider>
    </>
  );
}

export default App;

