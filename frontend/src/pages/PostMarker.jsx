import { Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png",
});


function PostMarker({postInfo, markerClicked}) {
    return (
    <Marker
      key={postInfo.id}
      position={postInfo.position}
      eventHandlers={{
        click: () => markerClicked(postInfo),
      }}
    >
      <Popup>{postInfo.name}</Popup>
    </Marker>
  );
}

export default PostMarker;