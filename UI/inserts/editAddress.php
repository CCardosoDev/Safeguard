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
        session_start();
        parse_str($_SERVER['QUERY_STRING']);

        if (isset($_POST['profileID']))
            $profileID = $_POST['profileID'];
        if (isset($_POST['profileID']))
            $adrID = $_POST['adrID'];
        if (isset($_POST['action']))
            $action = $_POST['action'];

        
        
        include_once '../classes/profile.php';
        include_once '../classes/address.php';
        include_once '../funcs.php';
                include_once '../conf.php';

        if ($action == 'editAddress') {
            if (!isset($_POST['submit'])) {
                echo '<h1>You have done it again, you silly!</h1>';
                exit();
            }

            $adress = new address();

            $adress->streetAddress = $_POST['street'];
            $adress->locality = $_POST['locality'];
            $adress->postCode = $_POST['post'];
            $adress->countryName = $_POST['country'];
        }

        $json = getProfiles();
        $profiles = json_decode($json);




        foreach ($profiles as $profile) {



            if ($profile->profileID == $profileID) {

                if ($action != 'editAddress') {
                    unset($profile->addresses[$adrID]);
                    $profile->addresses = array_values($profile->addresses);
                } else {
                    $profile->addresses[$adrID] = $adress;
                }
                $profile->token = $_SESSION['token'];
                echo $insert = editProfile($profile);
                break;
            }
        }

        if (empty($insert))
            if ($action == 'deleteAddress') {

                header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=profileAddress&profileID=".$profileID."&method=Address%20deleted%20sucessfully");
            } else {
                header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=profileAddress&profileID=".$profileID."&method=Address%20edited%20sucessfully");
            } else {
            echo $insert;
        }
        ?>
    </body>
</html>
