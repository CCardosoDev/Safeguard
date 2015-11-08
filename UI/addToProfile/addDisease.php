<div class="row-fluid">
    <div class="span12">
        <form action="./inserts/addDisease.php" method="POST">
            <fieldset>
                <legend>Diseases</legend>
                <label>Description</label>
                <input type="text" class="field span4" id="textarea" rows="6" name="desc"></textarea>
                <input type="hidden" name="profileID" value="<?parse_str($_SERVER['QUERY_STRING']); echo $profileID;?>"
                <p></p>
                <input class="btn btn-success" type="submit" name="submit" value="Submit">
            </fieldset>
        </form>
    </div>
</div>
