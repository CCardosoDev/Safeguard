<?php

include_once './API/georeferencingAccess.php';
include_once './API/calendarAccess.php';
include_once './conf.php';
include_once '../conf.php';

function getSSLPage($url) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
    curl_setopt($ch, CURLOPT_HEADER, false);
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_SSLVERSION, 3);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);

    $result = curl_exec($ch);
    echo curl_error($ch);
    curl_close($ch);
    return $result;
}

function insertUser($acesstoken) {
    $fields = json_encode(array("token" => $acesstoken));
    $postlength = strlen($fields);
    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_HTTPHEADER => array(
            'Content-Type: application/json',
            'Content-Length: ' . $postlength),
        CURLOPT_CUSTOMREQUEST => "POST",
        CURLOPT_URL => geoRefLink() . "/PofilesService/InsertUser",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS => $fields,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    curl_exec($ch);

    //echo close
    curl_close($ch);
}

function poke($acesstoken, $androidId) {
    $fileContent = file_get_contents("http://safeguardtracking.no-ip.org:8080/SafeguardTracking/webresources/poking/request/" . $acesstoken . "/" . $androidId);
    return $fileContent;
}

function lockPhone($acesstoken, $androidId) {
    $fileContent = file_get_contents("http://safeguardtracking.no-ip.org:8080/SafeguardTracking/webresources/locking/lock/" . $acesstoken . "/" . $androidId);
    return $fileContent;
}

function unLockPhone($acesstoken, $androidId) {
    $fileContent = file_get_contents("http://safeguardtracking.no-ip.org:8080/SafeguardTracking/webresources/locking/unlock/" . $acesstoken . "/" . $androidId);
    return $fileContent;
}

function requestPhoto($acesstoken, $androidId) {
    $fileContent = file_get_contents("http://safeguardtracking.no-ip.org:8080/SafeguardTracking/webresources/photo/request/" . $acesstoken . "/" . $androidId);
    return $fileContent;
}

function requestAudio($acesstoken, $androidId, $number) {
    $fileContent = file_get_contents("http://safeguardtracking.no-ip.org:8080/SafeguardTracking/webresources/telephony/request/" . $acesstoken . "/" . $androidId . "/" . $number);
    return $fileContent;
}

function deleteProfile($acesstoken, $profileID) {
    $fields = json_encode(array("token" => $acesstoken, "profileIDs" => array($profileID)));
    $postlength = strlen($fields);
    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_HTTPHEADER => array(
            'Content-Type: application/json',
            'Content-Length: ' . $postlength),
        CURLOPT_CUSTOMREQUEST => "POST",
        CURLOPT_URL => geoRefLink() . "/PofilesService/RemoveProfiles",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS => $fields,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    $resut = curl_exec($ch);

    //echo close
    curl_close($ch);
    return $resut;
}

function getProfiles() {
    /* var_dump($_SESSION['token']);
      $fileContent = file_get_contents(geoRefLink()."/PofilesService/GetProfiles?token=" . $_SESSION['token']);
      var_dump($fileContent);
     */
    error_reporting(~0);
    ini_set('display_errors', 1);
    $query = http_build_query(array('token' => $_SESSION['token']));
    $url = geoRefLink() . "/PofilesService/GetProfiles?" . $query;


    $fileContent = getSSLPage($url);


    return $fileContent;
}

function removeUser($acesstoken) {
    $fields = json_encode(array("token" => $acesstoken));
    $postlength = strlen($fields);
    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_HTTPHEADER => array(
            'Content-Type: application/json',
            'Content-Length: ' . $postlength),
        CURLOPT_CUSTOMREQUEST => "POST",
        CURLOPT_URL => geoRefLink() . "/PofilesService/RemoveUser",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS => $fields,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    curl_exec($ch);

    //echo close
    curl_close($ch);
}

function editProfile($profile) {


    $fields = json_encode($profile);
    $postlength = strlen($fields);

    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_HTTPHEADER => array(
            'Content-Type: application/json',
            'Content-Length: ' . $postlength),
        CURLOPT_CUSTOMREQUEST => "POST",
        CURLOPT_URL => geoRefLink() . "/PofilesService/EditProfile",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS => $fields,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    $result = curl_exec($ch);

    //echo close
    curl_close($ch);
    return $result;
}

function insertProfile($profile) {


    $fields = json_encode($profile);
    $postlength = strlen($fields);

    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_HTTPHEADER => array(
            'Content-Type: application/json',
            'Content-Length: ' . $postlength),
        CURLOPT_CUSTOMREQUEST => "POST",
        CURLOPT_URL => geoRefLink() . "/PofilesService/InsertProfile",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS => $fields,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    $result = curl_exec($ch);
    //echo close
    curl_close($ch);
    return $result;
}

function getServerAccess($service) {
    $fileContent = file_get_contents("https://ADSLKDJSCD:ASECSGJKMJHJHJH@" . authLink() . "/getServerAccess?token=" . $_SESSION['token'] . "&service=" . $service);
    $json = json_decode($fileContent, true);
    return $json["token"];
}

function handleAction() {
    parse_str($_SERVER['QUERY_STRING']);
    if ($action == 'logout') {
        session_destroy();
        unset($_SESSION['id']);
        unset($_SESSION['name']);
        unset($_SESSION['picture']);
        unset($_SESSION['token']);
        unset($_SESSION['mobileImagesRequest']);
        unset($_SESSION['hasNewImages']);
        unset($_SESSION['pokeReady']);
        unset($_SESSION['poke']);
        unset($_SESSION['gotOne']);
    }
}

function getJSON() {
    parse_str($_SERVER['QUERY_STRING']);
    if (is_null($token)) {
        return 'token not set';
    }

    $fileContent = file_get_contents("https://ADSLKDJSCD:ASECSGJKMJHJHJH@" . authLink() . "/getData?token=" . $token);

    if ($fileContent == '') {

        header("Location: https://" . redirectAuthLink() . "/login?redirecturl=http://" . redirectLink() . "/SafeFront/index.php"); /* Redirect browser */
        exit();
    }



    $json = json_decode($fileContent, true);


    if (!isset($_SESSION['id'])) {
        $_SESSION['id'] = getID($json);
        $_SESSION['name'] = getName($json);
        $_SESSION['picture'] = getImage($json);
        $_SESSION['mail'] = getEmail($json);
        $_SESSION['token'] = $token;

        $_SESSION['mobileImagesRequest'];
        $_SESSION['hasNewImages'] = 0;
        $_SESSION['gotOne'] = 0;
        $_SESSION['pokeReady'] = 0;
        $_SESSION['poke'];

        //$tokenToUse = getServerAccess();
        insertUser($token);
    }

    return null;
}
function getEmail($json) {
    return $json["userData"]["email"];
}
function getName($json) {
    return $json["userData"]["given_name"];
}

function getImage($json) {
    return $json["userData"]["picture"];
}

function getID($json) {
    return $json["userData"]["id"];
}

function checkIfLogged() {
    if (!isset($_SESSION['id'])) {
        echo '<div style="vertical-align: central"class="span4"><h1>You are not Logged</h1></div>';

        exit();
    }
}

function showSucess($method, $error) {
    if ($error == "0")
        echo '<div class="alert alert-success">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <h4>' . $method . '</h4>
</div>';
    else {
        echo '<div class="alert alert-danger">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <h4>' . $method . '</h4>
</div>';
    }
}

function getMobileImage($update) {

    $url = geoRefLink() . "/SafeGuard_Composition/GetPhoto?updateID=" . $update;
    $fileContent = getSSLPage($url);

    if ($fileContent != '{}') {

        $jsoninho = json_decode($fileContent);
        return $jsoninho->{'link'};
    }
    return null;
}

function getPoke() {
    if (!empty($_SESSION['poke'])) {
        error_reporting(~0);
        ini_set('display_errors', 1);
        strip_tags($_SESSION['poke']);
        $query = http_build_query(array('updateID' => $_SESSION['poke']));
        $url = geoRefLink() . "/SafeGuard_Composition/GetPoke?" . $query;

        $fileContent = getSSLPage($url);
        if ($fileContent != '{}') {
            $jsoninho = json_decode($fileContent);
            $_SESSION['poke'] = $jsoninho->{'poke'};
            $_SESSION['pokeReady'] = 1;
        }
    }
}

function getPhotos() {
    if (!empty($_SESSION['mobileImagesRequest'])) {

        foreach (array_keys($_SESSION['mobileImagesRequest']) as $request) {
            $resultado = getMobileImage($request);
            if (!is_null($resultado)) {
                $_SESSION['mobileImagesRequest'][$request] = $resultado;
                $_SESSION['hasNewImages']++;
                $_SESSION['gotOne'] = 1;
            }
        }
    }
}

function checkPhotos() {
    if ($_SESSION['hasNewImages'] > 0) {
        showSucess("Requested Photos Available", 0);
    }
}

function checkPokes() {
    if ($_SESSION['pokeReady'] > 0) {
        showSucess("Poke status available", 0);
    }
}

function getEventFences($calendarToken, $geoRefToken, $profileID) {
    $validEvents = json_decode(getAllEvents($calendarToken, $profileID), TRUE);

    $fences = (array) getFences($geoRefToken, $profileID);
    /* file_put_contents("simpleFences", "calendarToken :" . $calendarToken . "\n" .
      "geoRefToken :" . $geoRefToken . "\n" .
      "validEvents :" . print_r($validEvents["eventList"][$profileID], TRUE) . "\n" .
      "fences :" . print_r(getFences($geoRefToken, $profileID), TRUE)
      );
     */
    if (count($fences) == 0) {
        return array();
    }
    $fences = (array) $fences["return"];
    /*
      file_put_contents("getAllFences1", "calendarToken :" . $calendarToken . "\n" .
      "geoRefToken :" . $geoRefToken . "\n" .
      "validEvents :" . print_r($validEvents["eventList"][$profileID], TRUE) . "\n" .
      "fences :" . print_r($fences, TRUE)
      );
     */
    for ($j = 0; $j < count($fences); $j++) {
        $fences[$j] = (array) $fences[$j];
    }
    for ($j = 0; $j < count($fences); $j++) {
        if ($fences[$j]["id"] == -1) {
            unset($fences[$j]);
            $fences = array_values($fences);
            $j = count($fences);
        }
    }

    $result = array();
    for ($i = 0; $i < count($validEvents["eventList"][$profileID]); $i++) {
        $result[$i] = array();
        $result[$i]["event"] = $validEvents["eventList"][$profileID][$i];
        $fenceId = $validEvents["eventList"][$profileID][$i]["location"];
        for ($j = 0; $j < count($fences); $j++) {
            if ($fences[$j]["id"] == $fenceId) {
                $result[$i]["fence"] = $fences[$j];
                $j = count($fences);
            }
        }
    }


    return $result;
}

function getCurrentEventFences($calendarToken, $geoRefToken, $profileID) {
    $validEvents = json_decode(getValidEvents($calendarToken, $profileID), TRUE);
    $fences = (array) getFences($geoRefToken, $profileID);

    if (count($fences) == 0)
        return array();

    $fences = (array) $fences["return"];


    for ($j = 0; $j < count($fences); $j++) {
        $fences[$j] = (array) $fences[$j];
    }
    for ($j = 0; $j < count($fences); $j++) {
        if ($fences[$j]["id"] == -1) {
            unset($fences[$j]);
            $fences = array_values($fences);
            $j = count($fences);
        }
    }


    $result = array();
    for ($i = 0; $i < count($validEvents["eventList"][$profileID]); $i++) {
        $result[$i] = array();
        $result[$i]["event"] = $validEvents["eventList"][$profileID][$i];
        $fenceId = $validEvents["eventList"][$profileID][$i]["location"];
        for ($j = 0; $j < count($fences); $j++) {
            if ($fences[$j]["id"] == $fenceId) {
                $result[$i]["fence"] = $fences[$j];
                $j = count($fences);
            }
        }
    }


    return $result;
}

function sendMail($message) {
    ini_set("soap.wsdl_cache", "0");
    ini_set("soap.wsdl_cache_enabled", "0");
    $wsdl = "http://safeguardtracking.no-ip.org:8080/NotificationsService/NotificationsServiceImp?wsdl";

    $soapclient_options = array();
    $soapclient_options["verify_peer"] = false;
    $soapclient_options["verify_host"] = false;
    $soapclient_options["allow_self_signed"] = true;


    //var_dump($token);

    try {
        $client = new SoapClient($wsdl, $soapclient_options);
    } catch (SoapFault $e) {
        return 'Caught exception: ' . $e->getMessage() . "\n";
    }

    $params = array("sourceEmail" => "safeguardapp@gmail.com", "sourcePassword" => "appsafeguard", "destinationEmail" => "omarcelito@gmail.com", "subject" => "[CONTACT]", "body" => $message);
    try {

        return $client->sendEmail($params);
    } catch (Exception $e) {
        return 'Caught exception: ' . $e->getMessage() . "\n";
    }

    return NULL;
}

function sendReport($token, $profileId, $mail, $subject, $message, $initDate, $endDate) {

    $link = geoRefLink() . "/SafeGuard_Composition/CreatePDFProfile";
    $link = $link . "?token=" . $token;
    $link = $link . "&profileID=" . $profileId;
    $link = $link . "&destinationEmail=" . $mail;
    $link = $link . "&subject=" . $subject;
    $link = $link . "&message=" . $message;
    $link = $link . "&initDate=" . $initDate;
    $link = $link . "&endDate=" . $endDate;

    $ch = curl_init();
    $curlConfig = array(
        CURLOPT_CUSTOMREQUEST => "GET",
        CURLOPT_URL => $link,
        CURLOPT_RETURNTRANSFER => false,
        CURLOPT_SSLVERSION => 3,
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_VERBOSE => TRUE,
        CURLOPT_CERTINFO => TRUE);


    curl_setopt_array($ch, $curlConfig);

    //apenas executar n necessitamos do resultado
    $result = curl_exec($ch);
    file_put_contents("sendReport", curl_getinfo($ch, CURLINFO_HTTP_CODE) . "\n");
    //$result = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    //echo close
    curl_close($ch);

    //file_put_contents("getValidEvents_curl_getinfo", $result."\n" );

    return $result;
}

?>
