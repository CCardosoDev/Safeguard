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
        include_once '../classes/profile.php';
        include_once '../classes/address.php';
        include_once '../funcs.php';
                include_once '../conf.php';
        
        if (!isset($_POST['submit'])) {
            echo '<h1>You have done it again, you silly!</h1>';
            exit();
        }

        $personalPage = $_POST['desc'];
        


        $json = getProfiles();
        $profiles = json_decode($json);


        foreach ($profiles as $profile) {

            if ($profile->profileID == $_POST['profileID']) {


                array_push($profile->personalPages, $personalPage);
                $profile->token = $_SESSION['token'];

                echo $insert = editProfile($profile);

                break;
            }
        }

        if (empty($insert))
            header("Location: http://" . homeLink()  . "/SafeFront/index.php?action=personalPages&profileID=".$_POST['profileID']."&method=Allergy%20added%20Sucessfully"); 
        else {
            echo $insert;
        }
        ?>
    </body>
</html>
