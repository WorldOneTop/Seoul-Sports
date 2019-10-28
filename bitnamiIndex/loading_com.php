<?php
 include("connect.php");

	$op = "select head, date, num from list where name='".$_POST['list']."' ORDER BY num DESC limit ".$_POST['page'].", 3;";
	
	date_default_timezone_set('Asia/Seoul');
  	$now =  date("Y-m-d H:i:s");
  	$i =0;
  	foreach($mysqli->query($op) as $field){
  	$i++;
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

    $inner_html = '<blockquote>' ;
    $inner_html .= '<h3 class = "of">' . $field['head'] . '</h3>';
    $inner_html .= '<h6>' . $date . '</h6>';
    $inner_html .= '</blockquote>';
    echo $inner_html;
  }


?>