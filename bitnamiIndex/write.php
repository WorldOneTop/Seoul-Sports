<?php
session_start();
include("connect.php");

$sqlin = array("--","'",'"',"#");
$_POST['body'] = str_replace($sqlin, "", $_POST['body']);
$_POST['list'] = str_replace($sqlin, "", $_POST['list']);
$_POST['head'] = str_replace($sqlin, "", $_POST['head']);




 date_default_timezone_set('Asia/Seoul');
 $now =  date("Y-m-d H:i:s");

$op = "insert into list(head, body, user, date, name,user_id) values('".$_POST['head']."', '".$_POST['body']."', '".$_SESSION["name"]."', '".$now."', '".$_POST['list']."', '".$_SESSION['id']."');";

$mysqli->query($op);
	echo '<script>document.getElementById("forStr").innerHTML = "글 작성이 완료되었습니다.";</script>';
  echo "<script>toost();window.location.href=document.referrer;</script>";


?>