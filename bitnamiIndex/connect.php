<?php
    $host = 'localhost';
    $user = 'visitor';
    $pw = 'visitor';
    $dbName = 'test';
    $mysqli = new mysqli($host, $user, $pw,$dbName);

	if ($mysqli->connect_errno) {		
		die('Connection Error : '.$mysqli->connect_error); 
	}

?>