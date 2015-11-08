<?php

include_once './funcs.php';

if (!isset($_SESSION['id'])) {
    session_start();
}

checkIfLogged();

echo '<h1>Requested Poke</h1><p></p><p></p><p></p><p></p>';
if ($_SESSION['pokeReady'] > 0) {
    echo 'Status: ' . $_SESSION['poke'];
    $_SESSION['pokeReady'] = 0;
}
else {
    echo '<h4>Poke is not available yet</h4>';
}
?>
