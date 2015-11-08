<?php

if (!isset($_SESSION['id'])) {
    session_start();
}
checkIfLogged();
include_once './funcs.php';
include_once './API/georeferencingAccess.php';
include_once './API/calendarAccess.php';
parse_str($_SERVER['QUERY_STRING']);
$json = getProfiles();
$profiles = json_decode($json);


//file_put_contents("testPost","clean\n");
//$profileID = //atribuido no index
//get them tokens

$calendarToken = getServerAccess("calendar");
$geoRefToken = getServerAccess("georeferencing");


//check if post
if ($_SERVER['REQUEST_METHOD'] != "DELETE") {
    header("HTTP/1.0 404 Not Found");
    die("Only Deletes\n");
}

$result = deleteEvent($calendarToken, $profileID, $eventID);
removeFence($geoRefToken, $profileID, $fenceID);

/*
file_put_contents("1testDelete1", "calendarToken :" . $calendarToken . "\n" .
        "geoRefToken : " . $geoRefToken . "\n" .
        "profileID : " . print_r($profileID, TRUE) . "\n" .
        "eventID : " . print_r($eventID, TRUE) . "\n" .
        "fenceID : " . print_r($fenceID, TRUE) . "\n" .
        "event result : " . $result . "\n".
        "#\n"
);
*/