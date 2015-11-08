<?php

include_once './funcs.php';

if (!isset($_SESSION['id'])) {
    session_start();
}

checkIfLogged();

if (isset($_POST['sub'])) {
    if (!is_null($_POST['message']) and !is_null($_POST['name'])) {
        $result = sendMail("From: " . $_POST['name'] . "  Message: " . $_POST['message']);
            header("Location: http://" . homeLink()  . "/SafeFront/index.php?method=Your%20message%20was%20sent"); /* Redirect browser */
       
    }
} else {
    echo '<h1>You have done it again, you silly!</h1>';
    exit();
}
?>