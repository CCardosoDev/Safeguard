<?php
include_once './conf.php';
/*
  $token = "hVH2tPJ4UEFBuJkqXDETuN";
  $profileID = "10";
  ini_set("soap.wsdl_cache", "0");
  ini_set("soap.wsdl_cache_enabled", "0");
  $wsdl = $configs["authLink"] . '/GeoreferencingService/Georeferencing?WSDL';
  $sslOpts = array(
  'ssl' => array('ciphers' => 'RC4-SHA')
  );
  $soapclient_options = array();
  $soapclient_options["verify_peer"] = false;
  $soapclient_options["verify_host"] = false;
  $soapclient_options["allow_self_signed"] = true;
  $soapclient_options["stream_context"] = stream_context_create($sslOpts);

  try {
  $client = new SoapClient($wsdl, $soapclient_options);
  } catch (SoapFault $e) {
  echo 'Caught exception: ', $e->getMessage(), "\n";
  }
  $options = array(new SoapParam("token", $token), new SoapParam("childUserID", $profileID));
  try {
  $client->createUser("createUser", $options);
  } catch (Exception $e) {
  echo 'Caught exception: ', $e->getMessage(), "\n";
  } */

function getCompleteCoordinateHistory($token, $profileID) {
    ini_set("soap.wsdl_cache", "0");
    ini_set("soap.wsdl_cache_enabled", "0");
    $wsdl = geoRefLink().'/GeoreferencingService/Georeferencing?WSDL';
    $sslOpts = array(
        'ssl' => array('ciphers' => 'RC4-SHA')
    );
    $soapclient_options = array();
    $soapclient_options["verify_peer"] = false;
    $soapclient_options["verify_host"] = false;
    $soapclient_options["allow_self_signed"] = true;
    $soapclient_options["stream_context"] = stream_context_create($sslOpts);

    //var_dump($token);

    try {
        $client = new SoapClient($wsdl, $soapclient_options);
    } catch (SoapFault $e) {
        echo 'Caught exception: ', $e->getMessage(), "\n";
    }
    //$options = array(new SoapParam("token", $token), new SoapParam("childUserID", $profileID));

    $params = array("token" => $token, "childUserID" => $profileID);
    try {

        return $client->getCompleteCoordinateHistory($params);
    } catch (Exception $e) {
        echo 'Caught exception: ', $e->getMessage(), "\n";
    }

    return NULL;
}

function createUser($token, $profileID) {
    ini_set("soap.wsdl_cache", "0");
    ini_set("soap.wsdl_cache_enabled", "0");
    $wsdl = geoRefLink().'/GeoreferencingService/Georeferencing?WSDL';
    $sslOpts = array(
        'ssl' => array('ciphers' => 'RC4-SHA')
    );
    $soapclient_options = array();
    $soapclient_options["verify_peer"] = false;
    $soapclient_options["verify_host"] = false;
    $soapclient_options["allow_self_signed"] = true;
    $soapclient_options["stream_context"] = stream_context_create($sslOpts);

    //var_dump($token);

    try {
        $client = new SoapClient($wsdl, $soapclient_options);
    } catch (SoapFault $e) {
        echo 'Caught exception: ', $e->getMessage(), "\n";
    }
    //$options = array(new SoapParam("token", $token), new SoapParam("childUserID", $profileID));

    $params = array("token" => $token, "childUserID" => $profileID);
    try {
        return $client->createUser($params);
    } catch (Exception $e) {
        echo 'Caught exception: ', $e->getMessage(), "\n";
    }

    return NULL;
}

function insertFence($token, $profileID, $coordinates) {
    ini_set("soap.wsdl_cache", "0");
    ini_set("soap.wsdl_cache_enabled", "0");
    $wsdl = geoRefLink().'/GeoreferencingService/Georeferencing?WSDL';
    $sslOpts = array(
        'ssl' => array('ciphers' => 'RC4-SHA')
    );
    $soapclient_options = array();
    $soapclient_options["verify_peer"] = false;
    $soapclient_options["verify_host"] = false;
    $soapclient_options["allow_self_signed"] = true;
    $soapclient_options["stream_context"] = stream_context_create($sslOpts);

    //var_dump($token);

    try {
        $client = new SoapClient($wsdl, $soapclient_options);
    } catch (SoapFault $e) {
        echo 'Caught exception: ', $e->getMessage(), "\n";

        //file_put_contents("SoapFault", "1\n" .$e->getMessage() . "\n");
    }
    //$options = array(new SoapParam("token", $token), new SoapParam("childUserID", $profileID));

    $params = array("token" => $token, "childUserID" => $profileID, "coordinates" => $coordinates);
    try {
        return $client->insertFence($params);
    } catch (Exception $e) {
        echo 'Caught exception: ', $e->getMessage(), "\n";
        //file_put_contents("SoapFault", "2\n" . $e->getMessage() . "\n");
    }

    return -1;
}

function getFences($token, $profileID) {
    ini_set("soap.wsdl_cache", "0");
    ini_set("soap.wsdl_cache_enabled", "0");
    $wsdl = geoRefLink().'/GeoreferencingService/Georeferencing?WSDL';
    $sslOpts = array(
        'ssl' => array('ciphers' => 'RC4-SHA')
    );
    $soapclient_options = array();
    $soapclient_options["verify_peer"] = false;
    $soapclient_options["verify_host"] = false;
    $soapclient_options["allow_self_signed"] = true;
    $soapclient_options["stream_context"] = stream_context_create($sslOpts);

    //var_dump($token);

    try {
        $client = new SoapClient($wsdl, $soapclient_options);
    } catch (SoapFault $e) {
        echo 'Caught exception: ', "1\n" . $e->getMessage(), "\n";
    }
    //$options = array(new SoapParam("token", $token), new SoapParam("childUserID", $profileID));

    $params = array("token" => $token, "childUserID" => $profileID);
    try {
        return $client->getFences($params);
    } catch (Exception $e) {
        echo 'Caught exception: ',"2\n" . $e->getMessage(), "\n";
    }

    return NULL;
}

function removeFence($token, $profileID, $fenceID) {
    ini_set("soap.wsdl_cache", "0");
    ini_set("soap.wsdl_cache_enabled", "0");
    $wsdl = geoRefLink().'/GeoreferencingService/Georeferencing?WSDL';
    $sslOpts = array(
        'ssl' => array('ciphers' => 'RC4-SHA')
    );
    $soapclient_options = array();
    $soapclient_options["verify_peer"] = false;
    $soapclient_options["verify_host"] = false;
    $soapclient_options["allow_self_signed"] = true;
    $soapclient_options["stream_context"] = stream_context_create($sslOpts);

    //var_dump($token);

    try {
        $client = new SoapClient($wsdl, $soapclient_options);
    } catch (SoapFault $e) {
        echo 'Caught exception: ', $e->getMessage(), "\n";
    }
    //$options = array(new SoapParam("token", $token), new SoapParam("childUserID", $profileID));

    $params = array("token" => $token, "childUserID" => $profileID, "fenceID" => $fenceID);
    try {
        $client->removeFence($params);
    } catch (Exception $e) {
        echo 'Caught exception: ', $e->getMessage(), "\n";
    }

    return;
}