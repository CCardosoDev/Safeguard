
<?php

include_once './funcs.php';

if (!isset($_SESSION['id'])) {
    session_start();
}
checkIfLogged();


$json = getProfiles();
$profiles = json_decode($json);
parse_str($_SERVER['QUERY_STRING']);


echo '<div class="span8 offset1">';
echo '<h1>Profiles On Mobile</h1>';
$hasDroid = false;
foreach ($profiles as $profile) {
    if (isset($profile->androidID)) {
        $hasDroid = true;
        break;
    }
}
if ($hasDroid) {
    echo'<table class="table table-hover">';
    echo '<tr><th style="text-align:center">Action</th><th style="text-align:center"> Picture </th><th style="text-align:center"> Full Name </th></tr>';


    foreach ($profiles as $profile) {
        if (isset($profile->androidID)) {
            echo '<tr><td style="text-align:center">
        <a href="#">
        <li class="dropdown badge"><a class="btn dropdown-toggle" data-toggle="dropdown"></a>
                        <ul class="dropdown-menu">
                            <li><a href="./androidAction/action.php?action=poke&androidID=' . $profile->androidID . '">Poke</a></li>
                            <li><a href="./androidAction/action.php?action=lock&androidID=' . $profile->androidID . '">Lock</a></li>
                            <li><a href="./androidAction/action.php?action=unlock&androidID=' . $profile->androidID . '">Unlock</a></li>
                            <li><a href="./androidAction/action.php?action=photo&androidID=' . $profile->androidID . '">Request Photo</a></li>
                            <li><a href="#">Request Audio<form action="./androidAction/action.php?action=audio&androidID=' . $profile->androidID . '" method="POST">
            <fieldset>
                <input type="text" class="input-small" id="textarea" placeholder="Number to call" name="number">
                <input class="btn btn-primary" style="vertical-align: top" type="submit" name="submit" value="Submit">
            </fieldset>
        </form></li>
                        </ul>
                    </li></a></td><td style="text-align:center"><img width="40" height="40" src="' . $profile->photoLink . '"></td><td style="text-align:center">' . $profile->fullName . '</td></tr>';
        }
    }echo '</table>';
} else {
    echo '<h2> You have no android assosiated profiles</h2>';
}
echo '</div>';
echo '</div>';
?>
