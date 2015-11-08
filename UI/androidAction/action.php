<?php

include_once '../funcs.php';
include_once '../conf.php';

if (!isset($_SESSION['id'])) {
    session_start();
}

checkIfLogged();

parse_str($_SERVER['QUERY_STRING']);



$result;
switch ($action) {
    case 'poke':
        $result = poke($_SESSION['token'], $androidID);
        $xml = new SimpleXMLElement($result);
        
        $result = (string) $xml;
        if ($result != 'error') {
            $_SESSION['poke'] = $result;
        }
        break;
    case 'lock':
        $result = lockPhone($_SESSION['token'], $androidID);
        break;
    case 'unlock':
        $result = unLockPhone($_SESSION['token'], $androidID);
        break;
    case 'photo':
        $result = requestPhoto($_SESSION['token'], $androidID);
        $xml = new SimpleXMLElement($result);
        
        $result = (string) $xml;
        if ($result != 'error') {
            $_SESSION['mobileImagesRequest'][$result] = 0;
        }
        break;
    case 'audio':
        $result = requestAudio($_SESSION['token'], $androidID, $_POST['number']);
        break;
    default :
        break;
}




if ($result == "error") {
    header("Location: http://" . homeLink() . "/SafeFront/index.php?action=androidActions&method=" . $action . "%20was%20not%20sucessfull&error=1&result=" . $result);
} else {
    header("Location: http://" . homeLink() . "/SafeFront/index.php?action=androidActions&method=" . $action . "%20sucessfull&error=0&result=" . $result);
}
?>
