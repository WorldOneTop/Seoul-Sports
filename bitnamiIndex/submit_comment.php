<?php
	
session_start();
include("connect.php");

date_default_timezone_set('Asia/Seoul');
$now =  date("Y-m-d H:i:s");

$sqlin = array("--","'",'"',"#");

$_POST['num'] = str_replace($sqlin, "", $_POST['num']);




if($_POST['load']==2){
  $_POST['val'] = str_replace($sqlin, "", $_POST['val']);

	$op = 'insert into comment(body,list_num,user,date,user_id) values("'.$_POST['val'].'",'.$_POST['num'].',"'.$_SESSION["name"].'","'.$now.'","'.$_SESSION['id'].'")';
	$mysqli->query($op);
  echo '<script>window.location.reload()</script>';
}


else{
  $_POST['page'] = str_replace($sqlin, "", $_POST['page']);

	$op = "select body, user, date,user_id,num from comment where list_num='".$_POST['num']."' ORDER BY num DESC limit ".$_POST['page'].", 15;";

	foreach($mysqli->query($op) as $field){
	 $result = strtotime($now) - strtotime($field['date']);

      if($result <60*60){
        $date = (int)($result/60)."분 전";
      }
      else if($result < 60*60*24){
        $date = date('H시 i분',strtotime($field['date']));
      }
      else {
        $date = date('Y-m-d', strtotime($field['date']));
      }
      $h ="<script>commentNum = ".$field['num'].";</script>";
      if(!empty($_SESSION['id'])){
    	 if(!strcmp($_SESSION['id'],$field['user_id'])){
$h .= '<button type="button" class="menu btn-sm btn btn-default" data-toggle="modal" data-target="#modal_c"';
$h .= '>삭 제</button>';
      }}
      $h .= "<h5 style=\"display: inline;\">";
      $h .= $field['body'];
      $h .="</h5>";
      $h .= '<ul class="list-inline linee"><li><h5><b>&nbsp&nbsp'.$field['user'].'</b></h5></li><li><h6>'.$date.'</h6></li></ul>';
      // $h .= '';
      echo $h;
  }

}
?>