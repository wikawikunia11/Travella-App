import './App.css';
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import UserProfile from './pages/UserProfile';
import Home from './pages/Home';
import Login from './pages/Login';
import Registration from './pages/Registration';
import UserEdit from './pages/UserEdit';
import UserPosts from './pages/UserPosts';
import NewPostForm from './pages/NewPostForm';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/profile/:username" element={<UserProfile />} />
        <Route path="/profile/:username/edit" element={<UserEdit />} />
        <Route path="/profile/:username/posts" element={<UserPosts />} />
        <Route path="/addpost/:username" element={<NewPostForm />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Registration />} />
      </Routes>
    </Router>
  );
}

export default App;

