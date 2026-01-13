import React, {useState, useEffect } from 'react';
import { toast } from "react-toastify";
import { useParams, useNavigate } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import styles from './NewPostForm.module.css';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { useUser } from '../UserContext';

function normalizeLng(lng) {
  return ((lng + 180) % 360 + 360) % 360 - 180;
}

function LocationMarker({setPosition, localize }) {
  const map = useMapEvents({
    click(e) {
      const lat = e.latlng.lat;
      const lng = e.latlng.lng;
      setPosition([lat, lng]);
    },
    locationfound(e) {
      const lat = e.latlng.lat;
      const lng = e.latlng.lng;
      setPosition([lat, lng]);
      map.flyTo([lat, lng], map.getZoom())
    }
  })

  useEffect(() => {
    if (localize) {
      map.locate();
    }
  }, [localize, map]);
  return null;
}

export default function NewPostForm() {
  const { username } = useParams();
  const [date, setDate] = useState(new Date());
  const [position, setPosition] = useState(null);
  const [localize, setLocalize] = useState(false);
  const { user, token } = useUser();
  const navigate = useNavigate();
  const [previews, setPreviews] = useState([]);
  const [selectedFiles, setSelectedFiles] = useState([]);

  function handleImages(e) {
    const files = Array.from(e.target.files);
    const newFiles = files.filter(file => !selectedFiles.some(f => f.name === file.name));
    setSelectedFiles(prev => [...prev, ...newFiles]);

    setPreviews(prev => [...prev, ...newFiles.map(file => URL.createObjectURL(file))]);
    e.target.value = null;
  }

  function addPost(e) {
    e.preventDefault();

    if (!position) {
      alert("Select location first");
      return;
    }

    const formData = new FormData();

    formData.append("caption", e.target.caption.value);
    formData.append("description", e.target.description.value);
    formData.append("latitude", position[0]);
    formData.append("longitude", normalizeLng(position[1]));
    formData.append("visitDate", date.toISOString().split('T')[0]);
    selectedFiles.forEach(file => formData.append("images", file));
    setSelectedFiles([]);
    setPreviews([]);

    fetch('http://localhost:8080/api/posts/all', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formData
    })
    .then(res => {
      if (res.ok) {
        toast.success("Post added successfully!");
        navigate(`/profile/${username}/posts`);
      } else {
        alert("An error occurred while adding the post.");
      }
    })
    .catch(err => console.error(err));
  }
  if (user === null || user.username !== username)
    return (
        <h2>Access forbidden</h2>
    )
  return (
    <div className={styles.root}>
      <div className={styles.left_panel}>
        <h2>Add a new post
          <p className={styles.username}>{username}</p>
        </h2>
        <form className={styles.input} onSubmit={addPost}>
          <p className={styles.above_input}>Caption</p>
          <input name="caption" className={styles.input_box} placeholder="Quick caption"/>
          <p className={styles.above_input}>Description</p>
          <textarea name="description" className={styles.input_box} style={{height: "300px", resize: 'none'}} placeholder="Post description" />
          <p className={styles.above_input}>Pick visiting date</p>
          <DatePicker className={styles.date_picker} showMonthYearDropdown={true}  dateFormat={'dd - MM - YYYY'} selected={date} onSelect={e => setDate(e)} />
          <div className={styles.checkbox}>
            <p> Localize me</p>
            <input
            type="checkbox"
            checked={localize}
            onChange={(e) => setLocalize(e.target.checked)} />
          </div>
          <p className={styles.above_input}>Photos</p>
          <input
            type="file"
            name="images"
            accept="image/*"
            multiple
            className={styles.input_box}
            onChange={handleImages}
          />
          <div className={styles.preview}>
            {previews.map((src, i) => (
            <img key={i} src={src} width="100" />))}
          </div>
          <button type="submit" className={styles.button_box}>
            <p className={styles.button_text}>Add post</p>
          </button>
        </form>
      </div>
      <div
        className={styles.map_view}
        style={{
          border: "2px solid #646cff",
          borderRadius: "8px",
          overflow: "hidden",
        }}
      >
        <MapContainer
          center={[52.2297, 21.0122]}
          zoom={13}
          style={{ width: "100%", height: "100%" }}

        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution="&copy; OpenStreetMap"
          />
          <LocationMarker setPosition={setPosition} localize={localize}/>
          { position ? (
            <Marker position={position}>
              <Popup>chosen spot</Popup>
            </Marker>
          ) : (null)}
        </MapContainer>
      </div>
    </div>
  )
}
