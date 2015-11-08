<?php

include_once '../funcs.php';
if (!isset($_SESSION['id'])) {
    session_start();
}
checkIfLogged();


parse_str($_SERVER['QUERY_STRING']);
deleteProfile($_SESSION['token'], $profileID);



header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=listProfiles&method=Profile%20deleted%20sucessfully");
?>
