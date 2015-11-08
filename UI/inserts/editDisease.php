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
            $diseaseID = $_POST['diseaseID'];
        if (isset($_POST['action']))
            $action = $_POST['action'];

        
        

        include_once '../funcs.php';
                include_once '../conf.php';

        if ($action == 'editDisease') {
            if (!isset($_POST['submit'])) {
                echo '<h1>You have done it again, you silly!</h1>';
                exit();
            }


            }

        $json = getProfiles();
        $profiles = json_decode($json);




        foreach ($profiles as $profile) {



            if ($profile->profileID == $profileID) {
                echo '<p>Depois</p>';
                if ($action != 'editDisease') {
                    unset($profile->diseases[$diseaseID]);
                    $profile->diseases = array_values($profile->diseases);
                } else {
                    $profile->diseases[$diseaseID] = $_POST['desc'];
                }
                $profile->token = $_SESSION['token'];
                echo $insert = editProfile($profile);
                break;
            }
        }

        if (empty($insert))
            if ($action == 'deleteDisease') {

                header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=profileDiseases&profileID=".$profileID."&method=Disease%20deleted%20sucessfully");
            } else {
                header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=profileDiseases&profileID=".$profileID."&method=Disease%20edited%20sucessfully");
            } else {
            echo $insert;
        }
        ?>
    </body>
</html>
