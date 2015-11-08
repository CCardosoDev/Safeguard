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
if ($_SERVER['REQUEST_METHOD'] != "POST") {
    header("HTTP/1.0 404 Not Found");
    die("Only Posts\n");
}



//get the data
$requestDataJSON = file_get_contents("php://input");
$requestData = (array) json_decode($requestDataJSON, true);



$coordinates = (array) $requestData["fence"];

//file_put_contents("0testPost", "2testPost\n" );

/*
  for ($i = 0; $i <= count($coordinates); $i++) {
  $coordinates[$i] =  (array)$coordinates[$i];
  } */

$fenceID = "0";
$calendarData = (array) $requestData["event"];

//file_put_contents("0testPost", "3testPost\n" );

$fenceId = (array) insertFence($geoRefToken, $profileID, $coordinates);
$calendarData["location"] = (string) $fenceId["return"];

//file_put_contents("0testPost", "4testPost\n" );
//save coordiantes

insertEvent($calendarToken, $profileID, json_encode(array("event" => $calendarData)));

//file_put_contents("0testPost", "5testPost\n" );

/*
file_put_contents("0testPost", "calendarToken :" . $calendarToken . "\n" .
        "geoRefToken :" . $geoRefToken . "\n" .
        "coordinates :" . print_r($coordinates, TRUE) . "\n" .
        "fenceId :" . print_r($fenceId["return"], TRUE) . "\n" .
        "requestData" . json_encode(array("event" => $calendarData)) . "\n"
);
*/