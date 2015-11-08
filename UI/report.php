<?php
if (!isset($_SESSION['id'])) {
    session_start();
}
checkIfLogged();
include_once './funcs.php';


$json = getProfiles();
$profiles = json_decode($json);

parse_str($_SERVER['QUERY_STRING']);
//get the right profile
foreach ($profiles as $profile) {
    if ($profile->profileID == $profileID) {
        $profileInfo = $profile;
        break;
    }
}
?>

<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap-combined.min.css" rel="stylesheet"/>
<link rel="stylesheet" type="text/css" media="screen" href="http://tarruda.github.com/bootstrap-datetimepicker/assets/css/bootstrap-datetimepicker.min.css"/>

<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> 
<!--script type="text/javascript" src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js"></script-->
<script type="text/javascript" src="http://tarruda.github.com/bootstrap-datetimepicker/assets/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="http://tarruda.github.com/bootstrap-datetimepicker/assets/js/bootstrap-datetimepicker.pt-BR.js"></script>

<script>
    var profileId = <?php echo $profileID; ?>;
    $(function() {
        $('#datetimepicker1 ').datetimepicker({
            format: 'yyyy-MM-dd',
            language: 'pt-BR',
            pickTime: false
        });
        $('#datetimepicker2').datetimepicker({
            format: 'yyyy-MM-dd',
            language: 'pt-BR',
            pickTime: false
        });

    });
    function sendToMe()
    {
        
        sendMail("_");
    }
    function sendToOthers()
    {
        
        sendMail(document.getElementById("others").value);
    }


    function sendMail(mail)
    {
        var subject = document.getElementById("subject").value;
        
        var message = document.getElementById("message").value;


        var initDate = document.getElementById("startDate").value;
        
        var endDate = document.getElementById("endDate").value;
        

        //alert("profileID=" + profileId + "&subject=" + subject + "&message=" + message + "&mail=" + mail + "&initDate=" + initDate + "&endDate=" + endDate);
        xmlhttp = new XMLHttpRequest();
        xmlhttp.open('POST', "./index.php?action=reportPost&profileID=" + profileId + "&subject=" + subject + "&message=" + message + "&mail=" + mail + "&initDate=" + initDate + "&endDate=" + endDate, false);
        xmlhttp.setRequestHeader('Content-type', 'application/json; charset=utf-8');

        try {
            xmlhttp.send(null);
        } catch (err) {
            alert(err.description);
            //this prints "XMLHttprequest error: undefined" in the body.
        }
        //alert("./index.php?action=reportPost&profileID=" + profileId + "&subject" + subject + "&message=" + message + "&mail=" + mail + "&initDate=" + initDate + "&endDate=" + endDate);

        alert("Report successfully sent!");

    }


</script>

<div class="row-fluid"  style="margin-top: -10px; margin-left: 20px; margin-right:20px">
    <div class="row-fluid"  style="margin-left: -20px; margin-right:-20px">
        <div class="span10 offset1" >

            <legend>Generate Report : 

                <?
                echo '<img width="40" height="40" src="' . $profileInfo->photoLink . '">   ' . $profileInfo->fullName . '<p></p>';
                ?>
            </legend>
        </div>
        <div class="span4 offset1" >
            <label>From:</label>
            <div id="datetimepicker1" class="input-append date">
                <input id="startDate" name="startdate"  required="true" data-format="yyyy-MM-dd" type="text" style="width: 100%;"/>
                <span class="add-on" >
                    <i data-time-icon="icon-time" data-date-icon="icon-calendar" ></i>
                </span>
            </div>
        </div>
        <div class="span4" >
            <label>To:</label>
            <div id="datetimepicker2" class="input-append date">
                <input  id="endDate" name="endDate"  required="true" data-format="yyyy-MM-dd" type="text" style="width: 100%;"/>
                <span class="add-on" >
                    <i data-time-icon="icon-time" data-date-icon="icon-calendar" ></i>
                </span>
            </div>
        </div>
        <div class="span6 offset1" >
            <label>Subject:</label>
            <input id="subject"type="text" name="subject" required="true" maxlength="35"  style="width: 97%;"/>
        </div>
        <div class="span6 offset1" >
            <label>Message:</label>
            <textarea id="message" type="text" name="message" required="true" rows="4" maxlength="500" style="resize: none;  width: 97%;"></textarea>
        </div>
    </div>
    <div class="span10 offset1" ></div>
    <div class="span10 offset1 pull-left" >
        <button class="btn btn-primary" onclick="sendToMe();" >Send to me</button>
    </div>
    <div class="span10" >
    </div>
    <div class="span10 offset1 pull-left" >
        <div>
            <span class="label label-info">Or Send to:</span>   
        </div>
        <div class="input-append">
            <input id="others" type="email" name="email">
            <button class="btn btn-primary" onclick="sendToOthers();">Send</button>
        </div>
    </div>
</div>


