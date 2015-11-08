<?php
include_once './funcs.php';

if (!isset($_SESSION['id'])) {
    session_start();
}

checkIfLogged();

echo '<div class="span8 offset1"><h1>Requested Photos</h1><p></p><p></p><p></p><p></p>';
if ($_SESSION['gotOne'] > 0) {
    foreach ($_SESSION['mobileImagesRequest'] as $link) {
        if (!is_null($link))
            echo '<p><img width="500" height="702" src="' . $link . '" class="img-rounded"></p>';
    }
    $_SESSION['hasNewImages'] = 0;
}
else {
    echo '<h4>No images available</h4>';
}
echo '</div></div>';
?>
