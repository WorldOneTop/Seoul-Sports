  <?php
 include("connect.php");

 if(!empty($_POST['login'])){
  echo "<script>toost();</script>";
 }
	$op = "select head, date, num from list where name='".$_POST['list']."' ";
	  
  if(!empty($_POST['search'])){
    $search_replace = str_replace(' ','%',$_POST['search']);
    $op .='AND (head like "%'.$search_replace.'%" or body like "%'.$search_replace.'%") ';
  }
  
  $op .= "ORDER BY num DESC limit ".$_POST['page'].", 15;";
   $exists = mysqli_fetch_row($mysqli->query($op));
   

   if(empty($exists[0])){
  echo "<p class='text-center'><h3>게시글이 없습니다.</h3></p>";
}   
else{
	date_default_timezone_set('Asia/Seoul');
  	$now =  date("Y-m-d H:i:s");
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

      $inner_html = '<tr><td  onclick="location.href=\'view_text.html?list='.$_POST['list'].'&num='.$field['num'].'\'" >' ;
      $inner_html .= '<h3 class = "of"> ' . $field['head'] . '</h3>';
      $inner_html .= '<h6> ' . $date . '</h6>';
      $inner_html .= '</td></tr>';
      echo $inner_html;
  }
}


?>