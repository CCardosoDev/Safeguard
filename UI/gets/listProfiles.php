<?php

if (!isset($_SESSION['id'])) {
    session_start();
}
checkIfLogged();
include_once './funcs.php';

$json = getProfiles();
$profiles = json_decode($json);
parse_str($_SERVER['QUERY_STRING']);


echo '<h1>My Profiles</h1>';



echo'<table class="table table-hover">';
echo '<tr><th style="text-align:center"><i class="icon-plus"></i> Info </th><th style="text-align:center"> Picture </th><th style="text-align:center"> Full Name </th><th style="text-align:center"> Gender </th><th style="text-align:center"> Blood Type </th><th style="text-align:center"> Email </th><th style="text-align:center"> Observations </th></tr>';

if(!empty($profiles))
usort($profiles, "custom_sort");

// Define the custom sort function
function custom_sort($a, $b) {
    return strcasecmp($a->fullName, $b->fullName);
}

foreach ($profiles as $profile) {

    echo '<tr><td style="text-align:center">
        <a href="#">
        <li class="dropdown badge"><a class="btn dropdown-toggle" data-toggle="dropdown"></a>
                        <ul class="dropdown-menu">
                            <li><a href="./index.php?action=profileAddress&profileID=' . $profile->profileID . '">Addresses</a></li>
                            <li><a href="./index.php?action=profileDiseases&profileID=' . $profile->profileID . '">Diseases</a></li>
                            <li><a href="./index.php?action=profileAllergies&profileID=' . $profile->profileID . '">Allergies</a></li>
                            <li><a href="./index.php?action=personalPages&profileID=' . $profile->profileID . '">Personal Pages</a></li>
                            <li><a href="./index.php?action=profilePhones&profileID=' . $profile->profileID . '">Phones</a></li>
                            <li><a href="./index.php?action=profileEmails&profileID=' . $profile->profileID . '">Emails</a></li>
                            <li><a href="./index.php?action=addNewFence&profileID=' . $profile->profileID . '">Insert New Fence</a></li>
                            <li><a href="./index.php?action=manageFences&profileID=' . $profile->profileID . '">List Fences</a></li>
                                <li><a href="./index.php?action=monitor&profileID=' . $profile->profileID . '">Monitor</a></li>
                            <li class="divider"></li>
                            <li><a href="./index.php?action=deleteProfile&profileID=' . $profile->profileID . '">Delete Profile!</a></li>
                            <li class="divider"></li>
                            <li><a href="./index.php?action=report&profileID=' . $profile->profileID . '">Generate report</a></li>
                            
                        </ul>
                    </li></a></td><td style="text-align:center"><img width="40" height="40" src="' . $profile->photoLink . '"></td><td style="text-align:center">' . $profile->fullName . '</td><td style="text-align:center">' . $profile->gender . '</td><td style="text-align:center">' . $profile->bloodType . '</td><td style="text-align:center">';
    if (isset($profile->emails[0]))
        echo $profile->emails[0] . '</td><td style="text-align:center">' . $profile->observations . '</td></tr>';
    else {
        echo ' </td><td style="text-align:center">' . $profile->observations . '</td></tr>';
    }
}





echo '</table>';
?>
