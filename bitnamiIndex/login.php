<?php
session_start();
include("connect.php");
$sqlin = array("--","'",'"',"#");

$_POST['userid'] = str_replace($sqlin, "", $_POST['userid']);
$_POST['password'] = str_replace($sqlin, "", $_POST['password']);

$op = "select * from user where id='".$_POST['userid']."' and passwd = '".$_POST['password']."'";


$check = mysqli_fetch_row($mysqli->query($op));
if(empty($check[0])){
	$h = '<script>document.getElementById("forStr").innerHTML = "아이디 또는 비밀번호를 확인해주세요.";</script>';
	$h.= "<script>toost();</script>";
	echo $h;
}
else{

	$op = "select nickname, id from user where id='".$_POST['userid']."';";
	$check = mysqli_fetch_row($mysqli->query($op));
	$_SESSION["name"]=$check[0];
	$_SESSION["id"]=$check[1];

	$h = '<script>document.getElementById("forStr").innerHTML = "로그인 되었습니다.";toost();</script>';
	$h .= "<script>setTimeout(function() {history.back();},1000);</script>";
	echo $h;
}


?>