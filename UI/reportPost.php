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


//check if post
if ($_SERVER['REQUEST_METHOD'] != "POST") {
    header("HTTP/1.0 404 Not Found");
    die("Only Posts\n");
}

if ($mail == "_") {
    $mail = $_SESSION['mail'];
}

sendReport($_SESSION['token'], $profileID, $mail, $subject, $message, $initDate, $endDate);


file_put_contents("11testPost", "token : " . $_SESSION['token'] . "\n" .
        "profileID : " . $profileID . "\n" .
        "mail : " . $mail . "\n" .
        "subject : " . $subject . "\n" .
        "message : " . $message. "\n".
        "initDate : " . $initDate . "\n" .
        "endDate : " . $endDate . "\n");

