
<!DOCTYPE html>
<html lang="en">
    <?
    include_once './funcs.php';


    session_start();
    handleAction();
    if (!isset($_SESSION['id'])) {
        $pessoa = getJSON();
    }
    ?>

    <head>
        <meta charset="utf-8">
        <title>SafeGuard</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">



        <!-- Le styles -->
        <link href="./css/bootstrap.css" rel="stylesheet">
        <link href="./css/bootstrap-responsive.css" rel="stylesheet">
        <style type="text/css">
            body {
                padding-top: 60px;
                padding-bottom: 40px;
                #map-canvas { height: 100% }
            }
        </style>


        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="/js/html5shiv.js"></script>
        <![endif]-->

        <!-- Fav and touch icons -->
        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="/ico/apple-touch-icon-144-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="/ico/apple-touch-icon-114-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="/ico/apple-touch-icon-72-precomposed.png">
        <link rel="apple-touch-icon-precomposed" href="/ico/apple-touch-icon-57-precomposed.png">
        <link rel="shortcut icon" href="/ico/favicon.png">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> 
    </head>

    <body>
        <?
        include './header.php';
        ?>  

        <div class="container-fluid">

            <?
            parse_str($_SERVER['QUERY_STRING']);
            if(isset($_SESSION['id']))
            {
            getPhotos();
            getPoke();
            checkPhotos();
            checkPokes();
            }
            if (isset($method))
                if (isset($error))
                    showSucess($method, $error);
                else {
                    showSucess($method, 0);
                }
            if (!isset($action))
                $action = "ola";
            switch ($action) {
                case 'registerProfile':
                    include './registerProfile.php';
                    break;
                case 'sucessInsertProfile':
                    include './sucessInsertKid.php';
                    break;
                case 'listProfiles':
                    include './gets/listProfiles.php';
                    break;
                case 'profileAddress':
                    include './gets/profileAddress.php';
                    break;
                case 'profileDiseases':
                    include './gets/profileDiseases.php';
                    break;
                case 'profileAllergies':
                    include './gets/profileAllergies.php';
                    break;
                case 'personalPages':
                    include './gets/personalPages.php';
                    break;
                case 'profilePhones':
                    include './gets/profilePhones.php';
                    break;
                case 'profileEmails':
                    include './gets/profileEmails.php';
                    break;
                case 'addAddress':
                    include './addToProfile/addAddress.php';
                    break;
                case 'editAddress':
                    include './addToProfile/editAddress.php';
                    break;
                case 'deleteAddress':
                    include './inserts/editAddress.php';
                    break;
                case 'addNewFence':
                    include './inserts/drawFence.php';
                    break;
                case 'addNewFencePost':
                    include './inserts/drawFencePost.php';
                    break;
                case 'addDisease':
                    include './addToProfile/addDisease.php';
                    break;
                case 'editDisease':
                    include './addToProfile/editDisease.php';
                    break;
                case 'deleteDisease':
                    include './inserts/editDisease.php';
                    break;
                case 'addpersonalPage':
                    include './addToProfile/addpersonalPage.php';
                    break;
                case 'editpersonalPage':
                    include './addToProfile/editpersonalPage.php';
                    break;
                case 'deletepersonalPage':
                    include './inserts/editpersonalPage.php';
                    break;
                case 'addPhone':
                    include './addToProfile/addPhone.php';
                    break;
                case 'editPhone':
                    include './addToProfile/editPhone.php';
                    break;
                case 'deletePhone':
                    include './inserts/editPhone.php';
                    break;
                case 'addEmail':
                    include './addToProfile/addEmail.php';
                    break;
                case 'editEmail':
                    include './addToProfile/editEmail.php';
                    break;
                case 'deleteEmail':
                    include './inserts/editEmail.php';
                    break;
                case 'deleteProfile':
                    include './deleteProfile.php';
                    break;
                case 'androidActions':
                    include './androidAction/androidActions.php';
                    break;
                case 'requestedPhotos':
                    include './requestedPhotos.php';
                    break;
                case 'requestedPoke':
                    include './requestedPoke.php';
                    break;
                case 'manageFences':
                    include './gets/manageFences.php';
                    break;
                case 'deleteFence':
                    include './deletes/deleteFence.php';
                    break;
                case 'monitor':
                    include './gets/monitor.php';
                    break;
                case 'logout':
                    include './hero.php';
                    break;
                case 'report':
                    include './report.php';
                    break;
                case 'contact':
                    include './contact.php';
                    break;
                case 'about':
                    include './about.php';
                    break;
                case 'reportPost':
                    include './reportPost.php';
                    break;
                default :
                    if (isset($_SESSION['id'])) {
                        include './showAllKidsOnMap.php';
                    } else {
                        include './hero.php';
                    }
                    break;
            }
            ?>
            <!-- Example row of columns -->

            <hr>

            <? include './footer.php'; ?>


        </div> <!-- /container -->

        <!-- Le javascript
        ================================================== -->
        <!-- Placed at the end of the document so the pages load faster -->

        <script src="js/bootstrap.js"></script>
    </body>
</html>

