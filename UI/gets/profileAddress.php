<!--
To change this template, choose Tools | Templates
and open the template in the editor.
-->
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <?php
        if (!isset($_SESSION['id'])) {
            session_start();
        }
        checkIfLogged();
        include_once '../funcs.php';
        

        $json = getProfiles();
        $profiles = json_decode($json);
        parse_str($_SERVER['QUERY_STRING']);
        echo '<h1>Addresses</h1>';


        foreach ($profiles as $profile) {
            if ($profile->profileID == $profileID) {
                echo '<div class="span8 offset"><img width="40" height="40" src="' . $profile->photoLink . '">   ' . $profile->fullName . '<p></p></div>';
            }
        }

        echo'<table class="table table-hover">';
        echo '<tr><th style="text-align:center"> Action </th><th style="text-align:center"> Street Address </th><th style="text-align:center"> Locality </th><th style="text-align:center"> Postal Code </th><th style="text-align:center"> Country Name </th></tr>';

        foreach ($profiles as $profile) {

            if ($profile->profileID == $profileID) {
                $addresses = $profile->addresses;
                $adrID = 0;
                foreach ($addresses as $address) {
                    echo '<tr><td style="text-align:center"><a href="./index.php?action=editAddress&profileID=' . $profileID . '&adrID=' . $adrID . '"><i class="icon-pencil"></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="./index.php?action=deleteAddress&profileID=' . $profileID . '&adrID=' . $adrID . '"><i class="icon-trash"></i></a></td><td style="text-align:center">' . $address->streetAddress . '</td><td style="text-align:center">' . $address->locality . '</td><td style="text-align:center">' . $address->postCode . '</td><td style="text-align:center">' . $address->countryName . '</td></tr>';
                    $adrID++;
                }
                echo '<tr><td style="text-align:center"><a href="./index.php?action=addAddress&profileID=' . $profileID . '"><i class="icon-plus"></i></a></td><td></td><td></td><td></td><td></td>';
            }
        }





        echo '</table>';
        ?>
    </body>
</html>
