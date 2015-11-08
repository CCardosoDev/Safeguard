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
        //echo phpinfo();

        $fileContent = file_get_contents("https://ADSLKDJSCD:ASECSGJKMJHJHJH@".$authLink."/getServerAccess?token=j9cGMnHtYvvLC3jAGLfGbL&service=profiles");
        $json = json_decode($fileContent, true);
        $acesstoken = $json["token"];



        $fields = json_encode(array("token" => $acesstoken));
        $postlength = strlen($fields);
        
        //debugging
        //echo "<p> postlength :" . $postlength ."</p>";
        //echo "<p> JSON :" . $fields ."</p>";
        
        $ch = curl_init();
        $curlConfig = array(
            CURLOPT_HTTPHEADER => array(
                'Content-Type: application/json',
                'Content-Length: ' . $postlength),
            CURLOPT_CUSTOMREQUEST => "POST",
            CURLOPT_URL => $geoRefLink . "/PofilesService/InsertUser",
            
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_POSTFIELDS => $fields,
            CURLOPT_SSLVERSION => 3,
            CURLOPT_SSL_VERIFYPEER => FALSE,
            CURLOPT_SSL_VERIFYHOST => 0,
            CURLOPT_VERBOSE => TRUE,
            CURLOPT_CERTINFO => TRUE    );


        curl_setopt_array($ch, $curlConfig);


        $result = curl_exec($ch);
        
        //echo $result;
        curl_close($ch);
        ?>
    </body>
</html>


