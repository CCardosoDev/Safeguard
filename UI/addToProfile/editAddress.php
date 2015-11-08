<?
session_start();
include_once '../classes/profile.php';
include_once '../classes/address.php';
include_once '../funcs.php';
parse_str($_SERVER['QUERY_STRING']);

$json = getProfiles();
$profiles = json_decode($json);

foreach ($profiles as $profile) {

    if ($profile->profileID == $profileID) {
        $addresses = $profile->addresses;
    }
}
$addressToEdit = $addresses[$adrID];
?>


<div class="row-fluid">
    <div class="span12">
        <form action="./inserts/editAddress.php" method="POST">
            <fieldset>
                <legend>Address</legend>
                <label>Street</label>
                <input type="text" class="field span4" id="textarea" rows="6" name="street" value="<? echo $addressToEdit->streetAddress ?>"></textarea>
                <label>Locality</label>
                <input type="text" name="locality" value="<? echo $addressToEdit->locality ?>"> 
                <label>Post Code</label>
                <input type="text" name="post" value="<? echo $addressToEdit->postCode ?>">
                <label>Country</label>
                <input type="text" name="country" value="<? echo $addressToEdit->countryName ?>">
                <input type="hidden" name="profileID" value="<? echo $profileID; ?>">
                <input type="hidden" name="adrID" value="<? echo $adrID; ?>">
                <input type="hidden" name="action" value="<? echo $action; ?>">
                       <p></p>
                <input class="btn btn-success" type="submit" name="submit" value="Submit">
            </fieldset>
        </form>
    </div>
</div>
