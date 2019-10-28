<?php
session_start();
 
include("connect.php");

$sqlin = array("--","'",'"',"#");

$_POST['inputId'] = str_replace($sqlin, "", $_POST['inputId']);

$op = "select id from user where id='".$_POST['inputId']."'";
$check = mysqli_fetch_row($mysqli->query($op));

if(!empty($check[0])){
	echo '<script>faj("사용중인 아이디입니다.");</script>';
}
else{
	echo '<script>faj("회원가입 및 로그인이 되었습니다.");</script>';
	$op = "insert into user(id,passwd,nickname) values('";
	$op .= $_POST['inputId'];
	$op .= "','";
	$op .= $_POST['inputPassword'];
	$op .= "','";
	$op .= $_POST['inputname']."');";
	$mysqli->query($op);
	$_SESSION["name"]=$_POST['inputname'];
	$_SESSION["id"]=$_POST['inputId'];

	echo "<script>setTimeout(function() {window.history.go(-2);},1500);</script>";
}
?>