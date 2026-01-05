import React, {useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import styles from './NewPostForm.module.css';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

function LocationMarker({setPosition, localize }) {
  const map = useMapEvents({
    click(e) {
      setPosition([e.latlng.lat, e.latlng.lng])
    },
    locationfound(e) {
      setPosition([e.latlng.lat, e.latlng.lng])
      map.flyTo(e.latlng, map.getZoom())
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

  function addPost(e) {
    e.preventDefault();
    const formData = new FormData(e.target);

    var postInfo = {};
    formData.forEach(function(value, key){
        postInfo[key] = value;
    });
    postInfo["longitude"] = position[0];
    postInfo["latitude"] = position[1];
    postInfo["visit_date"] = date;
    var json = JSON.stringify(postInfo);

    console.log(json);
    }

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
          <DatePicker className={styles.date_picker} dateFormat={'dd - MM - YYYY'} selected={date} onSelect={e => setDate(e)} />
          <label className={styles.checkbox}> Localize me
            <input
            type="checkbox"
            checked={localize}
            onChange={(e) => setLocalize(e.target.checked)} />
          </label>
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
