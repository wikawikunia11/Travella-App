import React, { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { useParams, Link } from "react-router-dom";
import MapView from "./MapView";
import { useUser } from "../UserContext";
import styles from "./UserProfile.module.css";
import { PiSparkle } from "react-icons/pi";

const navButtonStyle = {
  backgroundColor: "rgba(255, 255, 255, 0.8)",
  border: "none",
  borderRadius: "50%",
  width: "36px",
  height: "36px",
  cursor: "pointer",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  fontSize: "18px",
  fontWeight: "bold",
  boxShadow: "0 2px 5px rgba(0,0,0,0.2)",
  transition: "background 0.3s",
};

function UserPosts() {
  const { username } = useParams();
  const { user, token, login, logout } = useUser();
  const [posts, setPosts] = useState([]);
  const [selectedMarker, setSelectedMarker] = useState(null);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!token || token === "null" || !token.includes('.')) {
      console.log(token);
      return;
    }

    setLoading(true);

    const userPosts = fetch(`http://localhost:8080/api/posts/user/${username}`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then(res => res.ok ? res.json() : []);

    const friendsPosts = (username === user.username)
      ? fetch(`http://localhost:8080/api/users/${username}/friends-posts`, {
          headers: { Authorization: `Bearer ${token}` },
        }).then(res => res.ok ? res.json() : [])
      : Promise.resolve([]);

    Promise.all([userPosts, friendsPosts])
      .then(([userPostsData, friendsPostsData]) => {
        setPosts([...userPostsData, ...friendsPostsData]);
      })
      .catch(err => {
        console.log(err);
        setError(err);
      })
      .finally(() => {
        setLoading(false);
      });

  }, [username, token, user.username]);
  

  const fetchImageWithToken = async (url) => {
    const res = await fetch(url, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) return null;
    const blob = await res.blob();
    return URL.createObjectURL(blob);
  };

  const fetchImages = async (postId) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/posts/${postId}/images`,
        {
        headers: { Authorization: `Bearer ${token}` },
      }
    );
      if (!res.ok) return [];
      const paths = await res.json();
      const urls = await Promise.all(
        paths.map((p) => fetchImageWithToken(`http://localhost:8080${p}`))
      );
      return urls.filter((u) => u !== null);
    } catch (err) {
      console.error(err);
      return [];
    }
  };



    const handleDeletePost = async () => {
    if (!window.confirm("Are you sure you want to delete this post?")) return;

    try {
      const res = await fetch(
        `http://localhost:8080/api/posts/${selectedMarker.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!res.ok) throw new Error("Delete failed");

      setPosts((prev) => prev.filter((p) => p.idPost !== selectedMarker.id));
      setSelectedMarker(null);
      toast.success("Post deleted");
    } catch (err) {
      console.error(err);
      alert("Could not delete post");
    }
  };

  const handleMarkerClick = async (post) => {
    const images = await fetchImages(post.id);
    setCurrentImageIndex(0);
    setSelectedMarker({ ...post, images });
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div
      style={{
      display: "flex",
      gap: "10px",
      width: "100%",
      backgroundColor: "#ffffff",
      margin: "10px",
      borderRadius: "24px",
      boxShadow: "0 10px 30px rgba(0, 0, 0, 0.08)",
      border: "1px solid rgba(0, 0, 0, 0.05)",
    }}
    >
      {/* LEFT SIDE - MAP AND DETAILS */}
      <div style={{
        flex: 1,
        padding: "20px",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: "20px",
        padding: "20px",
        overflowY: "auto"
      }}
      >
       <p style={{ fontSize: "20px" }}>
          The posts of <PiSparkle />
          <b>{username}</b>
          <PiSparkle />
        </p>
        <MapView
          key={posts.map((p) => p.idPost).join("-")}
          width="80%"
          height="400px"
          center={selectedMarker ? selectedMarker.position : undefined}
          markerData={posts.map((post) => ({
            id: post.idPost,
            name: post.caption,
            description: post.description,
            position: [post.latitude, post.longitude],
            username: post.user.username,
            isMine: post.user.username === user.username,
          }))}
          markerClicked={handleMarkerClick}
        />
        {selectedMarker ? (
          <div style={{ width: "100%", marginTop: "10px" }}>
            {/* Box posta */}
            <div
              style={{
                backgroundColor: "#ffffff",
                padding: "12px",
                borderRadius: "8px",
                marginBottom: "12px",
              }}
            >
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  position: "relative",
                  marginBottom: "6px",
                }}
              >
                <h3 style={{ margin: 0, textAlign: "center" }}>
                  {selectedMarker.name}
                </h3>

                {selectedMarker.username === user.username && (
                   <button
                    onClick={handleDeletePost}
                    style={{
                      position: "absolute",
                      right: "10px",
                      backgroundColor: "#c62828",
                      color: "white",
                      border: "none",
                      padding: "6px 10px",
                      borderRadius: "6px",
                      cursor: "pointer",
                    }}
                  >
                    Delete
                  </button>
                )}
              </div>
              <p style={{ textAlign: "center", marginTop: "10px" }}>{selectedMarker.description}</p>
            </div>

            {selectedMarker.images.length > 0 && (
              <div
              style={{
                marginTop: "15px",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                gap: "10px" }}
                >

                <div style={{ position: "relative", width: "100%", maxWidth: "500px" }}>
                  {selectedMarker.images.length > 1 && (
                    <button onClick={() => setCurrentImageIndex(prev => (prev === 0 ? selectedMarker.images.length - 1 : prev - 1))} style={{ ...navButtonStyle, position: "absolute", left: "10px", top: "50%", transform: "translateY(-50%)", zIndex: 10 }}>❮</button>
                  )}
                  <img src={selectedMarker.images[currentImageIndex]} alt="Post" style={{ width: "100%", height: "300px", borderRadius: "12px", objectFit: "cover" }} />
                  {selectedMarker.images.length > 1 && (
                    <button onClick={() => setCurrentImageIndex(prev => (prev === selectedMarker.images.length - 1 ? 0 : prev + 1))} style={{ ...navButtonStyle, position: "absolute", right: "10px", top: "50%", transform: "translateY(-50%)", zIndex: 10 }}>❯</button>
                  )}
                </div>
                <div style={{ display: "flex", gap: "5px" }}>
                  {selectedMarker.images.map((_, i) => (
                    <div key={i} onClick={() => setCurrentImageIndex(i)} style={{ width: "8px", height: "8px", borderRadius: "50%", backgroundColor: currentImageIndex === i ? "#225219ff" : "#ccc", cursor: "pointer" }} />
                  ))}
                </div>
              </div>
            )}
            <button onClick={() => setSelectedMarker(null)} style={{ display: "block", margin: "15px auto", background: "none", border: "none", color: "#666", textDecoration: "underline", cursor: "pointer" }}>Close details</button>
          </div>
        ) : (
          <p
            style={{
              marginTop: "20px",
              color: "#666"
            }}
            >Select a post from the list or map.</p>
        )}
      </div>

      {/* RIGHT SIDE: POST LIST */}
      <div style={{
        width: "300px",
        borderLeft: "1px solid #eee",
        backgroundColor: "#fcfcfc",
        display: "flex",
        flexDirection: "column",
        padding: "20px"
      }}>
        <h3 style={{ textAlign: "center", marginBottom: "20px" }}>Post List</h3>
        <div style={{ flex: 1, overflowY: "auto", display: "flex", flexDirection: "column", gap: "10px" }}>
          {posts.map((post) => (

            <button
              key={post.idPost}
              onClick={() => handleMarkerClick({
                id: post.idPost,
                name: post.caption,
                description: post.description,
                position: [post.latitude, post.longitude],
                username: post.user.username
              })}
              style={{
                padding: "12px",
                textAlign: "left",
                backgroundColor: selectedMarker?.id === post.idPost ? "#e8f5e9" : "#fff",
                border: selectedMarker?.id === post.idPost ? "2px solid #225219ff" : "1px solid #ddd",
                borderRadius: "10px",
                cursor: "pointer",
                fontWeight: selectedMarker?.id === post.idPost ? "bold" : "normal",
                transition: "all 0.2s"
              }}
            >
              {post.caption}
            </button>
          ))}
        </div>
        <Link to={`/profile/${username}/addpost`} style={{ textDecoration: "none", marginTop: "20px" }}>
          <button className={styles.button_box} style={{ backgroundColor: "#225219ff", width: "100%", margin: 0 }}>
            <p className={styles.button_text}>Add new post</p>
          </button>
        </Link>
      </div>
    </div>
  );
}

export default UserPosts;
