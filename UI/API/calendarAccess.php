<?php

include_once './conf.php';

function insertEvent($acesstoken, $profileId, $event) {

    $postlength = strlen($event);
    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_HTTPHEADER => array(
            'Content-Type: application/json',
            'Content-Length: ' . $postlength),
        CURLOPT_CUSTOMREQUEST => "POST",
        CURLOPT_URL => calendarLink() . "/event/" . $profileId . "?token=" . $acesstoken,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS => $event,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    curl_exec($ch);
    //file_put_contents("insertEvent_curl_getinfo", curl_exec($ch) . "\n" . "event = \n" . print_r($event,TRUE ) ."\n");
    $result = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    //echo close
    curl_close($ch);

    //file_put_contents("curl_getinfo", $result."\n" );

    return $result == 200;
}

function getValidEvents($acesstoken, $profileId) {

    $now = new DateTime();
    $date = $now->format('Y-m-d-H-i');


    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_CUSTOMREQUEST => "GET",
        CURLOPT_URL => calendarLink() . "/event/" . $profileId . "/" . $date . "/" . $date . "?token=" . $acesstoken,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    $result = curl_exec($ch);
    //file_put_contents("getValidEvents_curl_getinfo", curl_exec($ch) . "\n");
    //$result = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    //echo close
    curl_close($ch);

    //file_put_contents("getValidEvents_curl_getinfo", $result."\n" );

    return $result;
}

function getAllEvents($acesstoken, $profileId) {


    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_CUSTOMREQUEST => "GET",
        CURLOPT_URL => calendarLink() . "/event/" . $profileId . "?token=" . $acesstoken,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    $result = curl_exec($ch);
    //file_put_contents("getAllEvents_curl_getinfo", curl_exec($ch) . "\n");
    //curl_getinfo($ch, CURLINFO_HTTP_CODE);
    //echo close
    curl_close($ch);

    //file_put_contents("getAllEvents_curl_getinfo", $result."\n" );

    return $result;
}

function deleteEvent($acesstoken, $profileId, $event) {

    $postlength = strlen($event);
    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_CUSTOMREQUEST => "DELETE",
        CURLOPT_URL => calendarLink(). "/event/" . $profileId . "/" . $event . "?token=" . $acesstoken,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    curl_exec($ch);
    //file_put_contents("insertEvent_curl_getinfo", curl_exec($ch) . "\n" . "event = \n" . print_r($event,TRUE ) ."\n");
    $result = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    //echo close
    curl_close($ch);

    //file_put_contents("curl_getinfo", $result."\n" );

    return $result;
}
