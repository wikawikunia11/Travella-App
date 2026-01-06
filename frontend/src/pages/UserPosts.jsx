import React, { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { useParams, Link } from "react-router-dom";
import MapView from "./MapView";
import { useUser } from "../UserContext";
import styles from "./UserProfile.module.css";
import { PiSparkle } from "react-icons/pi";

function UserPosts() {
  const { username } = useParams();
  const { user, token, login, logout } = useUser();
  const [posts, setPosts] = useState([]);
  const [selectedMarker, setSelectedMarker] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchFriends = async () => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/users/${username}/friends`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!res.ok) return [];

      const data = await res.json();

      if (typeof data === "string") {
        console.error("Friends error:", data);
        return [];
      }

      return data.map((u) => u.username);
    } catch (err) {
      console.error(err);
      return [];
    }
  };

  useEffect(() => {
    const fetchAllPosts = async () => {
      try {
        setLoading(true);

        const friends = await fetchFriends();
        const users = [username, ...friends];

        const results = await Promise.all(
          users.map((u) =>
            fetch(`http://localhost:8080/api/posts/user/${u}`, {
              headers: { Authorization: `Bearer ${token}` },
            }).then((r) => (r.ok ? r.json() : []))
          )
        );

        const mergedPosts = results.flat();

        setPosts(mergedPosts);
      } catch (err) {
        console.error(err);
        setError("Failed to load posts");
      } finally {
        setLoading(false);
      }
    };

    fetchAllPosts();
  }, [username, token]);

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

  const removeMarkerById = (id) => {
    setPosts((prev) => prev.filter((p) => p.idPost !== id));
    if (selectedMarker && selectedMarker.id === id) {
      setSelectedMarker(null);
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

      removeMarkerById(selectedMarker.id);

      toast.success("Post deleted");
    } catch (err) {
      console.error(err);
      alert("Could not delete post");
    }
  };

  const handleMarkerClick = async (post) => {
    const images = await fetchImages(post.id);
    setSelectedMarker({ ...post, images });
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        gap: "10px",
        width: "100%",
        backgroundColor: "#ffffff",
        margin: "10px",
        borderRadius: "24px",
        boxShadow: "0 10px 30px rgba(0, 0, 0, 0.08)",
        border: "1px solid rgba(0, 0, 0, 0.05)",
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: "20px",
          padding: "20px",
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

              <p>{selectedMarker.description}</p>
            </div>

            {selectedMarker.images.length > 0 ? (
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(2, 1fr)",
                  gap: "10px",
                }}
              >
                {selectedMarker.images.map((url, index) => (
                  <img
                    key={index}
                    src={url}
                    alt={`Image ${index + 1}`}
                    style={{
                      width: "100%",
                      height: "auto",
                      borderRadius: "8px",
                      objectFit: "cover",
                    }}
                  />
                ))}
              </div>
            ) : (
              <p style={{ textAlign: "center", color: "#666" }}>No images</p>
            )}
          </div>
        ) : (
          <p>Click on a marker to see details.</p>
        )}
        <Link to={`/profile/${username}/addpost`} style={{ width: "100%" }}>
          <button
            className={styles.button_box}
            style={{ backgroundColor: "#225219ff" }}
          >
            <p className={styles.button_text}>Add new post</p>
          </button>
        </Link>
      </div>
    </div>
  );
}

export default UserPosts;
