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
            $allergyID = $_POST['allergyID'];
        if (isset($_POST['action']))
            $action = $_POST['action'];

        
        

        include_once '../funcs.php';
                include_once '../conf.php';

        if ($action == 'editAllergy') {
            if (!isset($_POST['submit'])) {
                echo '<h1>You have done it again, you silly!</h1>';
                exit();
            }


            }

        $json = getProfiles();
        $profiles = json_decode($json);




        foreach ($profiles as $profile) {



            if ($profile->profileID == $profileID) {
                if ($action != 'editAllergy') {
                    unset($profile->allergies[$allergyID]);
                    $profile->allergies = array_values($profile->allergies);
                } else {
                    $profile->diseases[$allergyID] = $_POST['desc'];
                }
                $profile->token = $_SESSION['token'];
                echo $insert = editProfile($profile);
                break;
            }
        }

        if (empty($insert))
            if ($action == 'deleteAllergy') {

                header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=profilepersonalPages&profileID=".$profileID."&method=Allergy%20deleted%20sucessfully");
            } else {
                header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=profilepersonalPages&profileID=".$profileID."&method=Allergy%20edited%20sucessfully");
            } else {
            echo $insert;
        }
        ?>
    </body>
</html>
