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
            $personalPageID = $_POST['personalPageID'];
        if (isset($_POST['action']))
            $action = $_POST['action'];

        
        

        include_once '../funcs.php';
                include_once '../conf.php';

        if ($action == 'editpersonalPage') {
            if (!isset($_POST['submit'])) {
                echo '<h1>You have done it again, you silly!</h1>';
                exit();
            }


            }

        $json = getProfiles();
        $profiles = json_decode($json);




        foreach ($profiles as $profile) {



            if ($profile->profileID == $profileID) {
                if ($action != 'editpersonalPage') {
                    unset($profile->personalPages[$personalPageID]);
                    $profile->personalPages = array_values($profile->personalPages);
                } else {
                    $profile->personalPages[$personalPageID] = $_POST['desc'];
                }
                $profile->token = $_SESSION['token'];
                echo $insert = editProfile($profile);
                break;
            }
        }

        if (empty($insert))
            if ($action == 'deletepersonalPage') {

                header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=personalPages&profileID=".$profileID."&method=Personal%20Page%20deleted%20sucessfully");
            } else {
                header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=personalPages&profileID=".$profileID."&method=Personal%20Page%20edited%20sucessfully");
            } else {
            echo $insert;
        }
        ?>
    </body>
</html>
