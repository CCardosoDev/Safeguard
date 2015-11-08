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
        $phones = $profile->phones;
    }
}
$phoneToEdit = $phones[$phoneID];
?>


<div class="row-fluid">
    <div class="span12">
        <form action="./inserts/editPhone.php" method="POST">
            <fieldset>
                <legend>Phone</legend>
                <label>Number</label>
                <input type="text" class="field span4" id="textarea" rows="6" name="desc" value="<? echo $phoneToEdit ?>"></textarea>
                <input type="hidden" name="profileID" value="<? echo $profileID; ?>">
                <input type="hidden" name="phoneID" value="<? echo $phoneID; ?>">
                <input type="hidden" name="action" value="<? echo $action; ?>">
                       <p></p>
                <input class="btn btn-success" type="submit" name="submit" value="Submit">
            </fieldset>
        </form>
    </div>
</div>
