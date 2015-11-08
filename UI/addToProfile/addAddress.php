<div class="row-fluid">
    <div class="span12">
        <form action="./inserts/insertAddress.php" method="POST">
            <fieldset>
                <legend>Address</legend>
                <label>Street</label>
                <input type="text" class="field span4" id="textarea" rows="6" name="street"></textarea>
                <label>Locality</label>
                <input type="text" name="locality"> 
                <label>Post Code</label>
                <input type="text" name="post">
                <label>Country</label>
                <input type="text" name="country">
                <input type="hidden" name="profileID" value="<?parse_str($_SERVER['QUERY_STRING']); echo $profileID;?>"
                <p></p>
                <input class="btn btn-success" type="submit" name="submit" value="Submit">
            </fieldset>
        </form>
    </div>
</div>
