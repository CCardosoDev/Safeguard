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
        $emails = $profile->emails;
    }
}
$emailToEdit = $emails[$emailID];
?>


<div class="row-fluid">
    <div class="span12">
        <form action="./inserts/editEmail.php" method="POST">
            <fieldset>
                <legend>Edit</legend>
                <label>Email</label>
                <input type="text" class="field span4" id="textarea" rows="6" name="desc" value="<? echo $emailToEdit ?>"></textarea>
                <input type="hidden" name="profileID" value="<? echo $profileID; ?>">
                <input type="hidden" name="emailID" value="<? echo $emailID; ?>">
                <input type="hidden" name="action" value="<? echo $action; ?>">
                       <p></p>
                <input class="btn btn-success" type="submit" name="submit" value="Submit">
            </fieldset>
        </form>
    </div>
</div>
