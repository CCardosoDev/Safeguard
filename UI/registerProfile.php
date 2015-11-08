
<?
checkIfLogged();

?>
<div class="row-fluid">
    <div class="span8 offset2">
        <form action="./inserts/insertProfile.php" method="POST" enctype="multipart/form-data" >
            <h1>Add Profile</h1>
            <fieldset>
                <legend>Profile</legend>
                <label>Name</label>
                <input type="text" name="name" required="true"/>
                <label>Age</label>
                <input type="number" name="bday"/>
                <label>Email</label>
                <input type="email" name="email" required="true"/>
                <label>Phone</label>
                <input type="text" name="phone"/>
                <label>Gender</label>
                <label class="radio">
                    <input type="radio" name="gender" id="r1" value="m" checked>
                    Male
                </label>
                <label class="radio">
                    <input type="radio" name="gender" id="r2" value="f">
                    Female
                </label>
                <label>Blood Type</label>
                <select class="selectpicker" name="blood">
                    <option>O-</option>
                    <option>O+</option>
                    <option>A-</option>
                    <option>A+</option>
                    <option>B-</option>
                    <option>B+</option>
                    <option>AB-</option>
                    <option>AB+</option>
                </select>
                <p></p>
                <label>Picture</label>

                <div style="position:relative;">
                    <a class='btn btn-primary' href='javascript:;'>
                        Choose File...
                        <input type="file" style='position:absolute;z-index:2;top:0;left:0;filter: alpha(opacity=0);-ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";opacity:0;background-color:transparent;color:transparent;' name="img" size="40"  onchange='$("#upload-file-info").html($(this).val().replace("C:\\fakepath\\", ""));'> 
                    </a>
                    &nbsp;
                    <span class='label label-info' id="upload-file-info"></span>
                </div>
                <p></p>
                <label>Observations</label>
                <textarea rows="4" cols="50" name="observation" placeholder="Optional..."></textarea>
                <p></p>

                <legend>Address</legend>
                <label>Street</label>
                <input type="text" class="field span4" id="textarea" rows="6" name="street"></textarea>
                <label>Locality</label>
                <input type="text" name="locality"> 
                <label>Post Code</label>
                <input type="text" name="post">
                <label>Country</label>
                <input type="text" name="country">
                <p></p>


                <input class="btn btn-success" type="submit" name="submit" value="Submit">
            </fieldset>
        </form>
    </div>
</div>



<?php ?>
