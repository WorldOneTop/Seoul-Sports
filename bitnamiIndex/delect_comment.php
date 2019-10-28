<?php
include("connect.php");

$now =  date("Y-m-d H:i:s");

$sqlin = array("--","'",'"',"#");
$_POST['num'] = str_replace($sqlin, "", $_POST['num']);



if($_POST['isComment']=='true'){
	$op = "delete from comment where num=".$_POST['num'].";";
	$mysqli->query($op);

 	$h ='<script>window.location.reload();</script>';
	$h .= "<script>faj('글이 삭제되었습니다.')</script>";
	echo $h;
}
else{
	echo "<script>faj('글이 삭제되었습니다.')</script>";

	$op = "delete from list where num=".$_POST['num'].";";
	$mysqli->query($op);

	$op = "delete from comment where list_num =".$_POST['num'].";";
	$mysqli->query($op);

	echo"<script>window.location.href=document.referrer;";
}
?>