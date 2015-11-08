<?
include_once './conf.php';
?>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="brand" href="./">Safe Guard</a>
            <div class="nav-collapse collapse">
                <ul class="nav">
                    <li class="active"><a href="./">Home</a></li>
                    <li><a href="./index.php?action=about">About</a></li>
                    <li><a href="./index.php?action=contact">Contact</a></li>
                    <? if (isset($_SESSION['id'])) {
                    
                    
                    echo '
                    <li class="dropdown">
                        <a href="#" id="drop3" role="button" class="dropdown-toggle" data-toggle="dropdown">Actions<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="./index.php?action=registerProfile">Add Profile</a></li>
                            <li><a href="./index.php?action=listProfiles">List Profiles</a></li>
                            <li class="divider"></li>
                            <li class="nav-header">Mobiles</li>
                            <li><a href="./index.php?action=androidActions">Android Actions</a></li>
                            <li><a href="./index.php?action=requestedPhotos">Requested Photos</a></li>
                            <li><a href="./index.php?action=requestedPoke">Requested Poke</a></li>
                        </ul>
                    </li>'; }?>
                </ul>
                <ul class="nav pull-right">
                    <?
                    $link = "https://".  redirectAuthLink() ."/login?redirecturl=http://" . redirectLink()  . "/SafeFront/index.php";
                    $imageSRC = "https://developers.google.com/+/images/branding/sign-in-buttons/Red-signin_Long_base_44dp.png";
                    $height = 150;
                    $widht = 150;
                    if (isset($_SESSION['id'])) {
                        $link = "http://" . homeLink()  . "/SafeFront/index.php?action=logout";
                        $imageSRC = "./img/LogOut.png";
                        $height = 70;
                        $widht = 70;
                        echo '<li class="active"><a href="">'.$_SESSION['name'].'</a></li>';
                        echo '<img style="" width="40" height="40" src="'.$_SESSION['picture'].'">';
                        echo '<a href="' . $link . '">&nbsp;&nbsp;<button class="btn btn-danger" style="vertical-align: top" type="button">Logout</button></a>';
                    }else
                    echo '<a href="' . $link . '"><img style="vertical-align: text-top;" width="'.$widht.'" height="'.$height.'"src="' . $imageSRC . '"></a>';
                    ?>
                </ul>
            </div><!--/.nav-collapse -->
        </div>
    </div>
</div>

