import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

// Naprawa ikon Leaflet w bundlerach (Vite/Webpack)
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png",
});

export default function MapView() {
  return (
    <MapContainer
      center={[52.2297, 21.0122]} // Warszawa
      zoom={13}
      style={{ height: "100vh", width: "100%" }}
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution="&copy; OpenStreetMap"
      />

      <Marker position={[52.2297, 21.0122]}>
        <Popup>To jest marker w Leaflet (MapView)</Popup>
      </Marker>
    </MapContainer>
  );
}
