
<?php
$photoLink = "http://t1.gstatic.com/images?q=tbn:ANd9GcRwzNtFf4N5sSsjz41Y9OZPoAl0abQ7nE-Pi3CR8487RmZJO9Wo";
$photoDefault = 'img/shield-final.png';
$locations = array(array("filho1", -34.397, 150.644, $photoLink), array("filho2", -32.397, 150.644, $photoDefault));
$locationsJSON = json_encode($locations);
$center = array(-34.397, 150.644);
$centerJSON = json_encode($center);

$fence1 = array(array(-33.050112271849656, 149.17193412780762), array(-32.6532508799688, 152.19866752624512), array(-32.133757156326446, 151.51202201843262), array(-31.95216223802496, 149.39166069030762), array(-32.95336814579931, 149.11150932312012), array(-33.050112271849656, 149.17193412780762));
$fence2 = array(array(-32.04533283885849, 148.63360404968262), array(-33.043205496165555, 149.3863821029663), array(-33.178939260581046, 150.6333303451538), array(-33.321348526698806, 151.1826467514038), array(-34.59704151614416, 152.01696395874023), array(-35.80890404406862, 151.06115341186523), array(-34.98500313017105, 147.71032333374023), array(-33.24787594792437, 148.02892684936523), array(-32.04533283885849, 148.63360404968262));
$fenceList = array($fence1, $fence2);
$fenceListJSON = json_encode($fenceList);
?>
<script type="text/javascript"
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCiqoYxEdGavaIMyluULmfODvqIgfxElmU&sensor=false">
</script>

<script type="text/javascript">

    var fenceList = <?php echo $fenceListJSON; ?>;
    var mapCenter = <?php echo $centerJSON; ?>;
    var markers = <?php echo $locationsJSON; ?>;
    var mapMarkers = new Array();
    var boundaryColor = '#326467'; // initialize color of polyline
    var polyCoordinates = []; // initialize an array where we store latitude and longitude pair
    var count = 0;
    var map;
    var Path;
    function makeInfoWindowEvent(map, infowindow, marker) {
        return function() {
            infowindow.open(map, marker);
        };
    }

    function initialize() {
        var mapOptions = {
            center: new google.maps.LatLng(mapCenter[0], mapCenter[1]),
            zoom: 8
        };
        map = new google.maps.Map(document.getElementById("map-canvas"),
                mapOptions);


        for (var i = 0; i < fenceList.length; i++)
        {
            convertLatLng(fenceList[i]);
            createPolyline(fenceList[i]);
        }

        for (var i = 0; i < markers.length; i++)
        {

            var ic = {
                url: markers[i][3],
                size: new google.maps.Size(100, 100),
                origin: new google.maps.Point(0, 0),
                anchor: new google.maps.Point(0, 0),
                scaledSize: new google.maps.Size(50, 50)
            };


            var m = new google.maps.Marker({
                position: new google.maps.LatLng(markers[i][1], markers[i][2]),
                map: map,
                icon: ic
                        //title: markers[i][0],
                        //text: markers[i][0]

            });

            var info = new google.maps.InfoWindow({
                content: markers[i][0]
            });

            m.info = info;
            mapMarkers[i] = m;

            m.info.open(map, m);

            google.maps.event.addListener(mapMarkers[i], 'click', makeInfoWindowEvent(map, mapMarkers[i].info, mapMarkers[i]));

        }
    }
    function connectPoints()
    {
        var point_add = []; // initialize an array
        var start = polyCoordinates[0]; // storing start point
        var end = polyCoordinates[(polyCoordinates.length - 1)]; // storing end point
        // pushing start and end point to an array
        point_add.push(start);
        point_add.push(end);
        createPolyline(point_add); // function to join points

        polyCoordinates[polyCoordinates.length] = polyCoordinates[0];
        alert(polyCoordinates);
        polyCoordinates = []; // initialize an array where we store latitude and longitude pair
        count = 0;


    }
    function createPolyline(polyC)
    {
        Path = new google.maps.Polyline({
            path: polyC,
            strokeColor: boundaryColor,
            strokeOpacity: 1.0,
            strokeWeight: 2
        });
        Path.setMap(map);
    }

    function convertLatLng(fenceArray)
    {
        for (var i = 0; i < fenceArray.length; i++)
        {
           
            fenceArray[i] = new google.maps.LatLng(fenceArray[i][0], fenceArray[i][1]);
        }
    }

    google.maps.event.addDomListener(window, 'load', initialize);
</script>


<div class="row-fluid"  style="margin-top: -10px; margin-left: 20px; margin-right:20px">
    <div class="row-fluid">
        <div class="span1" style="text-align: center; height:690px" >
            <input type=button name="btnconnect" value="Done" onclick="connectPoints();" />
        </div>
        <div class="span10 offset1" style="text-align: center; height:690px" >
            <div onload="initialize()" id="map-canvas" class="google-maps" style="height: 100%; width: 100%"/>
        </div>
    </div>
</div>
