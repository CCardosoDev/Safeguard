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
        $diseases = $profile->diseases;
    }
}
$diseaseToEdit = $diseases[$diseaseID];
?>


<div class="row-fluid">
    <div class="span12">
        <form action="./inserts/editDisease.php" method="POST">
            <fieldset>
                <legend>Disease</legend>
                <label>Description</label>
                <input type="text" class="field span4" id="textarea" rows="6" name="desc" value="<? echo $diseaseToEdit ?>"></textarea>
                <input type="hidden" name="profileID" value="<? echo $profileID; ?>">
                <input type="hidden" name="diseaseID" value="<? echo $diseaseID; ?>">
                <input type="hidden" name="action" value="<? echo $action; ?>">
                       <p></p>
                <input class="btn btn-success" type="submit" name="submit" value="Submit">
            </fieldset>
        </form>
    </div>
</div>
