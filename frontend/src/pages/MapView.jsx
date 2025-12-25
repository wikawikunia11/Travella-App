import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import PostMarker from "./PostMarker";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png",
});

export default function MapView({ width = "600px", height = "400px", markerData, markerClicked }) {
  // const defaultPosition = [52.2297, 21.0122];
  // const position = markerPosition || defaultPosition;
  const postList = markerData.map(m => <PostMarker postInfo={m} markerClicked={markerClicked}/>)

  return (
    <div
      style={{
        width,
        height,
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
        {postList}
      </MapContainer>
    </div>
  );
}
