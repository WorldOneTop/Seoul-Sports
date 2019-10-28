<?php

/*
session_start();

$_SESSION["name"]="qwer";//초기화
$_SESSION["id"]="asd";//서버에서는 값에 들어간걸로 확인

echo session_cache_expire();
echo "<br>";
echo session_id();

if($_SESSION["name"])		//접속자가 지금 세션에 접속중
	echo "ASD";
	*/
// $temp = array('a'=>1,'b'=>'서울숲');
// echo $temp['b'];




	  include("connect.php");
	  $op = "select * from list limit 0, 3;";
		$h="";	   
	  $i=0;
  	foreach($mysqli->query($op) as $field){
	  	$arr[$i] = array('num'=>$field['num'], 'user'=>$field['user']);
	  	$i++;
	  $h .= "<li>";
	  $h .= $field['num']."  ".$field['user'];
	  $h .= "<br> ".$_POST['page'];
	  $h .= "</li>";



	  }
	  echo $h;

	  


// 	  $my_array = array (

//     array (
//     'firstName' => 'Brett',
//     'lastName' => 'McLaughlin',
//     'email' => 'brett@newInstance.com',
//     ),
//     array (
//     'firstName' => 'Jason',
//     'lastName' => 'Hunter',
//     'email' => 'jason@servlets.com',
//     ),
//     array (
//     'firstName' => 'Isaac',
//     'lastName' => 'Asimov',
//     'genre' => 'science fiction',
//     ),
//     array (
//     'firstName' => 'Frank',
//     'lastName' => 'Peretti',
//     'genre' => 'christian fiction',
//     )
// );
// 	  echo json_encode($my_array);
	  
/*
echo json_encode($user, JSON_UNESCAPED_UNICODE );
header("Content-Type: application/json");



how to send
어떻게 보내지냐*/
?>