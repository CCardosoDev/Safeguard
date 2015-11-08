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
        
        if (isset($method)) {
            showSucess($method, 0);
        }
        checkIfLogged();
        include_once '../funcs.php';

        $json = getProfiles();
        $profiles = json_decode($json);
        parse_str($_SERVER['QUERY_STRING']);
        echo '<div class="span8 offset1">';
        echo '<h1>Personal Pages</h1>';
        
        foreach ($profiles as $profile) {
            if ($profile->profileID == $profileID) {
                echo '<div class="span8 offset"><img width="40" height="40" src="'.$profile->photoLink.'">   '.$profile->fullName.'<p></p></div>';
            }
        }
        
        echo'<table class="table table-hover">';
        echo '<tr><th style="text-align:center"> Action </th><th style="text-align:center"> Page </th></tr>';

        foreach ($profiles as $profile) {

            if ($profile->profileID == $profileID) {
                $pages = $profile->personalPages;
                $personalPage = 0;
                foreach ($pages as $page) {
                    echo '<tr><td style="text-align:center"><a href="./index.php?action=editpersonalPage&profileID=' . $profileID . '&personalPageID=' . $personalPage . '"><i class="icon-pencil"></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="./index.php?action=deletepersonalPage&profileID=' . $profileID . '&personalPageID=' . $personalPage . '"><i class="icon-trash"></i></a></td><td style="text-align:center">' . $page . '</td></tr>';
                    $personalPage++;
                }
                echo '<tr><td style="text-align:center"><a href="./index.php?action=addpersonalPage&profileID=' . $profileID . '"><i class="icon-plus"></i></a></td><td></td></tr>';
            }
        }





        echo '</table>';
        echo '</div>';
        echo '</div>';

        ?>
    </body>
</html>
