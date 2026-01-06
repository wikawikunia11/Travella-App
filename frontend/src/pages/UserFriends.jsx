import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from "react-router-dom";
import styles from './UserFriends.module.css';
import {FaSearchPlus} from "react-icons/fa";
import {LuSendHorizontal} from "react-icons/lu";
import { BsFillPersonPlusFill } from "react-icons/bs";
import { BsPersonDashFill } from "react-icons/bs";
import {useUser} from "../UserContext.jsx";

function UserFriends() {
    const { username } = useParams();
    const navigate = useNavigate();
    const { token } = useUser();
    const [friends, setFriends] = useState([]);
    const [newFriend, setNewFriend] = useState(""); // their username
    const [foundFriends, setFoundFriends] = useState([]); // found profile
    const [found, setFound] = useState(false);
    const [error, setError] = useState("");
    const [refresh, setRefresh] = useState(false);

    function processDataList(data) {
        return data.map(f => ({
            id: f.idUser,
            username: f.username,
            name: f.name,
            surname: f.surname,
            biography: f.biography,
            profilePic: f.profilePic || 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_640.png',
            profileUrl: `http://localhost:5173/profile/${f.username}`
        }))
    }

    useEffect(() => {
        setRefresh(false);
        fetch(`http://localhost:8080/api/users/${username}/friends`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
            if (!response.ok) throw new Error("Failed to fetch friends");
            return response.json();
            })
            .then(data => {
                const temp_friends = processDataList(data);
                setFriends(temp_friends);
            })
            .catch(err => { setError(err); console.log(err);});
    }, [username, token, refresh]);

    function handleSearch() {
        fetch(`http://localhost:8080/api/users/search?query=${newFriend}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(async response => {
            if (!response.ok) throw new Error(`Błąd: ${response.status}`);
            return response.json();
        })
        .then(data => {
            const temp_found = processDataList(data);
            setFoundFriends(temp_found);
            setFound(data.length > 0);
        })
        .catch(err => {
            setError(err.message);
            setFoundFriends([]);
            setFound(false);
        });
    }

    function handleAddFriend(friendUsername) {
        const requestOptions = {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
        };
        fetch(`http://localhost:8080/api/users/${friendUsername}/friends`, requestOptions)
        .then(response => {
            if (!response.ok) {
                setError("Couldn't add friend");
            }
            setRefresh(true);
            setFound(false);
            setNewFriend("");
            return response;
        })
        .catch(e => console.log(e));
        setRefresh(true);
        setFound(false);
        setNewFriend("");
    }

    function handleDeleteFriend(friendUsername) {
        const requestOptions = {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
        };
        fetch(`http://localhost:8080/api/users/${friendUsername}/friends`, requestOptions)
        .then(response => {
            if (!response.ok) {
                setError("Couldn't add friend");
            }
            setRefresh(true);
            return response;
        })
        .catch(e => console.log(e));
    }

    return (
      <div className={styles.main}>
          {error && <p>{error}</p>}
          <p className={styles.title}>These are <b>{username}'s</b> friends</p>
          {friends.length === 0 ? (
              <p>No friends yet</p>
          ) : (
              <ul className={styles.friendsList}>
                  {friends.map((friend) => (
                      <li key={friend.username} className={styles.friend}>
                          <img src={friend.profilePic} alt={friend.name} className={styles.img} />
                          <Link to={friend.profileUrl} className={styles.link}>
                              <span>@{friend.username}</span>
                          </Link>
                          <h3>{friend.name} {friend.surname}</h3>
                          <p className={styles.bio}>{friend.biography}</p>
                          <button className={styles.addButton} onClick={() => handleDeleteFriend(friend.username)}>
                            <BsPersonDashFill />
                            <p>Remove friend</p>
                        </button>
                      </li>
                  ))}
              </ul>
          )}
          <h3 style={{marginTop: '20px'}}>Search for new friends</h3>
          <div className={styles.searchBar}>
              <FaSearchPlus size={'25px'}/>
              <input name="new_friend" placeholder="Search by username" value={newFriend} onChange={(e) => {setNewFriend(e.target.value)}} />
              <button onClick={handleSearch}>
                <LuSendHorizontal size={'25px'}/>
              </button>
          </div>
          {found && <div className={styles.friendsList}>
              {foundFriends.map((friend) => (
                  <div key={friend.username} className={styles.friend}>
                      <img src={friend.profilePic} alt={friend.name} className={styles.img} />
                      <Link to={friend.profileUrl} className={styles.link}>
                          <span>@{friend.username}</span>
                      </Link>
                      <h3>{friend.name} {friend.surname}</h3>
                      <p className={styles.bio}>{friend.biography}</p>
                      <button className={styles.addButton} onClick={() => handleAddFriend(friend.username)}>
                          <BsFillPersonPlusFill />
                          <p>Add friend</p>
                      </button>
                  </div>
              ))}
          </div>}
      </div>
  )
}

export default UserFriends;