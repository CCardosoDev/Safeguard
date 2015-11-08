<?php
if (!isset($_SESSION['id'])) {
    session_start();
}
checkIfLogged();
include_once './funcs.php';
include_once './API/georeferencingAccess.php';

$json = getProfiles();
$profiles = json_decode($json);

parse_str($_SERVER['QUERY_STRING']);
//get the right profile
foreach ($profiles as $profile) {
    if ($profile->profileID == $profileID) {
        $profileInfo = $profile;
        break;
    }
}


//$profileID = //atribuido no index
//get them tokens

$calendarToken = getServerAccess("calendar");
$geoRefToken = getServerAccess("georeferencing");


//get latest coordinates of kiddo
$profilePositionList = (array) getCompleteCoordinateHistory($geoRefToken, $profileID);

if (count((array) $profilePositionList) == 0) {
    $profileLastPosition = array(40.6352500, -8.6583000); //default aveiro
    $locations = array();
} else {
    $profileLastPositionFenceId = (array) $profilePositionList["return"][0];
    $profileLastPositionCoordinate = (array) $profileLastPositionFenceId["coordinate"];
    $profileLastPosition = array($profileLastPositionCoordinate["latitude"], $profileLastPositionCoordinate["longitude"]);
    $locations = array(array($profileInfo->fullName, $profileLastPositionCoordinate["latitude"], $profileLastPositionCoordinate["longitude"], $profileInfo->photoLink));
}

$eventsFences = getCurrentEventFences($calendarToken, $geoRefToken, $profileID);
$eventsFencesJSON = json_encode($eventsFences);

$center = $profileLastPosition;
$locationsJSON = json_encode($locations);
$centerJSON = json_encode($center);
?>

<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap-combined.min.css" rel="stylesheet"/>
<link rel="stylesheet" type="text/css" media="screen" href="http://tarruda.github.com/bootstrap-datetimepicker/assets/css/bootstrap-datetimepicker.min.css"/>

<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> 
<!--script type="text/javascript" src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js"></script-->
<script type="text/javascript" src="http://tarruda.github.com/bootstrap-datetimepicker/assets/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="http://tarruda.github.com/bootstrap-datetimepicker/assets/js/bootstrap-datetimepicker.pt-BR.js"></script>

<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCiqoYxEdGavaIMyluULmfODvqIgfxElmU&sensor=false"></script>

<script type="text/javascript">

    var mapCenter = <?php echo $centerJSON; ?>;
    var markers = <?php echo $locationsJSON; ?>;
    var mapMarkers = new Array();
    var boundaryColor = '#0000FF'; // initialize color of polyline
    var boundaryColorOld = '#000000'; // initialize color of polyline
    var polyCoordinates = []; // initialize an array where we store latitude and longitude pair
    var count = 0;
    var map;
    var oldPathsMarkers = [];
    var Path;
    var fencesToDraw = <? echo $eventsFencesJSON; ?>;
    var validFence = false;

    var profileId = <?php echo $profileID; ?>;
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

        validFence = false;
        map = new google.maps.Map(document.getElementById("map-canvas"),
                mapOptions);

        //draw old fences
        for (var i = 0; i < fencesToDraw.length; i++)
        {
            var oldFence = fencesToDraw[i]["fence"]["coordinates"];


            var oldFenceLatLng = [];
            for (var j = 0; j < oldFence.length; j++)
            {
                oldFenceLatLng[j] = new google.maps.LatLng(oldFence[j]["latitude"], oldFence[j]["longitude"]);
            }

            createPolyline(oldFenceLatLng, boundaryColorOld);


            var info = new google.maps.InfoWindow({
                content: fencesToDraw[i]["event"]["summary"]
            });

            var m = new google.maps.Marker({
                position: new google.maps.LatLng(oldFence[0]["latitude"], oldFence[0]["longitude"]),
                map: map,
                //title: markers[i][0],
                //text: markers[i][0]

            });
            oldPathsMarkers[i] = m;
            oldPathsMarkers[i].info = info;
            //oldPathsMarkers[i].info.open(map, oldPathsMarkers[i]);
            google.maps.event.addListener(oldPathsMarkers[i], 'click', makeInfoWindowEvent(map, oldPathsMarkers[i].info, oldPathsMarkers[i]));
        }


        for (var i = 0; i < markers.length; i++)
        {

            var ic = {
                url: markers[i][3],
                size: new google.maps.Size(25, 25),
                origin: new google.maps.Point(0, 0),
                anchor: new google.maps.Point(0, 0),
                scaledSize: new google.maps.Size(25, 25)
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



        var h = $(window).height();
        $('#map-canvas').css('height', h - 165);
    }
    polyCoordinates = [];
    function connectPoints()
    {
        var point_add = []; // initialize an array
        var start = polyCoordinates[0]; // storing start point
        var end = polyCoordinates[(polyCoordinates.length - 1)]; // storing end point
        // pushing start and end point to an array
        point_add.push(start);
        point_add.push(end);
        createPolyline(point_add, boundaryColor); // function to join points

        polyCoordinates[polyCoordinates.length] = polyCoordinates[0];
        //alert(polyCoordinates);
        //polyCoordinates = []; // initialize an array where we store latitude and longitude pair
        count = 0;
        validFence = true;
        google.maps.event.clearListeners(map, 'click');

    }
    function cleanMap()
    {
        polyCoordinates = [];
        count = 0;
        initialize();
    }
    function createPolyline(polyC, color)
    {
        Path = new google.maps.Polyline({
            path: polyC,
            strokeColor: color,
            strokeOpacity: 1.0,
            strokeWeight: 2
        });
        Path.setMap(map);
    }
    google.maps.event.addDomListener(window, 'load', initialize);
</script>


<div class="row-fluid"  style="margin-top: -10px; margin-left: 20px; margin-right:20px">
    <div class="row-fluid"  style="margin-left: -20px; margin-right:-20px">
        <div class="span12" >

            
            <legend>Monitoring : 

                <?
                echo '<img width="40" height="40" src="' . $profileInfo->photoLink . '">   ' . $profileInfo->fullName . '<p></p>';
                ?>
            </legend>


            <div onload="initialize()" id="map-canvas" class="google-maps" style="height: 100%; width: 100%">


            </div>
        </div>
    </div>
</div>