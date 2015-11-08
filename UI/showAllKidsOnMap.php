<?php
if (!isset($_SESSION['id'])) {
    session_start();
}
checkIfLogged();
include_once './funcs.php';
include_once './API/georeferencingAccess.php';

$json = getProfiles();
$profiles = json_decode($json);
$geoRefToken = getServerAccess("georeferencing");
$locations = array();
$errorList = array();

foreach ($profiles as $profileInfo) {
    $profilePositionList = (array) getCompleteCoordinateHistory($geoRefToken, $profileInfo->profileID);
    if (count((array) $profilePositionList) == 0) {
        //add to error list
        $errorList[] = array("fullName" => $profileInfo->fullName, "photoLink" => $profileInfo->photoLink);
    } else {
        $profileLastPositionFenceId = (array) $profilePositionList["return"][0];
        $profileLastPositionCoordinate = (array) $profileLastPositionFenceId["coordinate"];

        $profileLastPosition = array($profileLastPositionCoordinate["latitude"],
            $profileLastPositionCoordinate["longitude"]);
        $locations[] = array($profileInfo->fullName,
            $profileLastPositionCoordinate["latitude"],
            $profileLastPositionCoordinate["longitude"],
            $profileInfo->photoLink,
            $profileInfo->profileID);
    }
}



$locationsJSON = json_encode($locations);
$center = array(40.6352500, -8.6583000);
$centerJSON = json_encode($center);
?>
<script type="text/javascript"
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCiqoYxEdGavaIMyluULmfODvqIgfxElmU&sensor=false">
</script>

<script type="text/javascript">

    var mapCenter = <?php echo $centerJSON; ?>;
    var markers = <?php echo $locationsJSON; ?>;
    var mapMarkers = new Array();


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
            m.profileID = markers[i][4];

            google.maps.event.addListener(mapMarkers[i], 'mouseover', makeInfoWindowEvent(map, mapMarkers[i].info, mapMarkers[i]));
            google.maps.event.addListener(mapMarkers[i], 'click', makeRedirect(mapMarkers[i].profileID));

        }

        var h = $(window).height();
        $('#map-canvas').css('height', h - 165);
    }
    function makeRedirect(profileID)
    {
        return function() {
            window.location = "./index.php?action=monitor&profileID=" + profileID;
        }
    }

    google.maps.event.addDomListener(window, 'load', initialize);

</script>


<?
if (count($errorList) == 0 and count($locations) == 0) {
    echo '<div class="alert alert-error">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <div class="row-fluid"  style="margin-top: -10px; margin-left: 20px; margin-right:20px">
        <div class="row-fluid"  style="margin-left: -20px; margin-right:-20px"> <div class="span12" >
                            <h4>No coordinates available!</h4>
                    </div>        </div>
    </div>
</div>';
} else if (count($errorList) > 0) {
    echo
    '<div class="alert alert-error">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <div class="row-fluid"  style="margin-top: -10px; margin-left: 20px; margin-right:20px">
        <div class="row-fluid"  style="margin-left: -20px; margin-right:-20px"><div class="span12" ><h4>No coordinates available for:</h4></div>';
    foreach ($errorList as $errorListElement) {
        echo ' <div class="span2" style="margin-left: 0px; margin-right:-10px" ><h5>';
        echo '<img width="20" height="20" src="' . $errorListElement["photoLink"] . '">   ' . $errorListElement["fullName"];
        echo '</h5></div>';
    }
    echo '        </div>
    </div>
</div>';
}
?>


<div class="row-fluid"  style="margin-top: -10px; margin-left: 20px; margin-right:20px">
    <div class="row-fluid"  style="margin-left: -20px; margin-right:-20px">
        <div class="span12" >
            <legend>Last known positions</legend>
            <div onload="initialize()" id="map-canvas" class="google-maps" style="height: 100%; width: 100%">
            </div>
        </div>
    </div>
</div>
<div class="row-fluid"  style="margin-top: -10px; margin-left: 20px; margin-right:20px">
    <div class="span12" ></div>
</div>