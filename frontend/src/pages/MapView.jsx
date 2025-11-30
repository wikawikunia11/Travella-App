import { Attribution, defaults, FullScreen, Rotate, ScaleLine,} from "ol/control"
import Map from "ol/Map"
import View from "ol/View"

export default function MapView() {
const mapTargetElement = useRef(null)
const [map, setMap] = useState(null)

 useEffect(() => {
        // Create OpenLayers map so we can display it to the user.
        const InitializeMap = new Map({
            // The collection of layers associated with this map.
            layers: [],
            /* The map's view allows to specify the center,
             * zoom, resolution, and rotation of the map.
             */
            view: new View({
                // The map view projection.
                projection: WEB_MERCATOR_COORDINATE_SYSTEM_ID, // "EPSG:3857"
                center: [0, 0],
                zoom: 0,
                minZoom: 0,
                maxZoom: 28,
            }),
            /* The map's default controls is a visible widget with a DOM
             * element in a fixed position on the map.
             */
            controls: defaults({ attribution: false }).extend([
                new Attribution({
                    collapsed: true,
                    collapsible: true,
                }),
                // Add a fullscreen button control to the map.
                new FullScreen(),
                // Add scale line control to the map.
                new ScaleLine(),
                // Add a reset rotation button control to the map.
                new Rotate(),
            ]),
        })

        // Set the Initialized map to the map target element.
        InitializeMap.setTarget(mapTargetElement.current || "")
        // Set the current map, so we can continue working with it.
        setMap(InitializeMap)

        /* We set map target to "undefined", an empty string to represent a
         * nonexistent HTML element ID, when the React component is unmounted.
         * This prevents multiple maps being added to the map container on a
         * re-render.
         */
        return () => InitializeMap.setTarget("")
    }, [])


    return (
        <div
            ref={mapTargetElement}
            className="map"
            style={{
                width: "100%",
                height: "100%",
                position: "relative",
            }}
        ></div>
    )
}
