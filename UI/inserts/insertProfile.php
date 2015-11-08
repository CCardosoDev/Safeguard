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
        <?
        session_start();
        include_once '../classes/profile.php';
        include_once '../classes/address.php';
        include_once '../funcs.php';
                include_once '../conf.php';

        $img = $_FILES['img'];

        echo 'ola';
        echo '</p>';

        //gets da info para passagem ao web service
        $profile = new profile();
        

        $profile->fullName = $_POST['name'];
        $profile->emails = array($_POST['email']);
        $profile->bday = $_POST['bday'];
        $profile->phones = array($_POST['phone']);
        $profile->gender = $_POST['gender'];
        $profile->bloodType = $_POST['blood'];
        if(isset($_POST['observation']))
        $profile->observations = $_POST['observation'];
        
        //morada
        $adress = new address();
        $adress->streetAddress = $_POST['street'];
        $adress->locality = $_POST['locality'];
        $adress->postCode = $_POST['post'];
        $adress->countryName = $_POST['country'];
        $profile->addresses = array($adress);
        $profile->diseases = "";
        $profile->allergies = "";
        $profile->personalPages = "";
        







        if (isset($_POST['submit'])) {
            if ($img['name'] == '') {
                if ($profile->gender == 'm')
                    $url = 'http://philanthropy.gatech.edu/sites/default/files/styles/bio-large/public/philanthropists/Archibald%20D.%20Holland/default_profile.jpg?itok=txush5CX';
                if ($profile->gender == 'f')
                    $url = 'http://thinktheology.co.uk/images/member_photos/default-user-image-female.jpg';
                $profile->photoLink = $url;
            } else {
                $filename = $img['tmp_name'];
                $client_id = "fa9eb43f8eacf44";
                $handle = fopen($filename, "r");
                $data = fread($handle, filesize($filename));
                $pvars = array('image' => base64_encode($data));
                $timeout = 30;
                $curl = curl_init();
                curl_setopt($curl, CURLOPT_URL, 'https://api.imgur.com/3/image.json');
                curl_setopt($curl, CURLOPT_TIMEOUT, $timeout);
                curl_setopt($curl, CURLOPT_HTTPHEADER, array('Authorization: Client-ID ' . $client_id));
                curl_setopt($curl, CURLOPT_POST, 1);
                curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
                curl_setopt($curl, CURLOPT_POSTFIELDS, $pvars);
                $out = curl_exec($curl);
                curl_close($curl);
                $pms = json_decode($out, true);
                $url = $pms['data']['link'];
                $profile->photoLink = $url;
                /* if ($url != "") {
                  echo "<h2>Uploaded Without Any Problem</h2>";
                  echo "<img src='$url'/>";
                  echo $url;
                  } else {
                  echo "<h2>There's a Problem</h2>";
                  echo $pms['data']['error']['message'];
                  } */
            }
        }


        $profile->token = $_SESSION['token'];
        

        $insert = insertProfile($profile);

        if(empty($insert))
            header("Location: http://" . redirectLink()  . "/SafeFront/index.php?action=listProfiles&method=Profile inserted sucessfully"); /* Redirect browser */
        else {
            echo $insert;
        }
        //TODO  chamar web services com isto tudo
        ?>
    </body>
</html>
