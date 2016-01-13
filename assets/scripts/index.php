<?php

/**
 * File to handle all API requests
 * Accepts GET and POST
 * 
 * Each request will be identified by TAG
 * Response will be JSON data

  /**
 * check for POST request 
 */
if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // get tag
    $tag = $_POST['tag'];

    // include db handler
    require_once 'DB_Functions.php';
    $db = new DB_Functions();

    // response Array
    $response = array("tag" => $tag, "success" => 0, "error" => 0);

    // check for tag type
    if ($tag == 'login') {
        // Request type is check Login
        $username = $_POST['username'];
        $password = $_POST['password'];

        // check for user
        $user = $db->getUserByUsernameAndPassword($username, $password);
        if ($user != false) {
            // user found
            // echo json with success = 1
            $response["success"] = 1;
            $response["user"]["id"] = $user["ID"];
            $response["user"]["name"] = $user["USERNAME"];
            $response["user"]["email"] = $user["EMAIL"];
            $response["user"]["creation_time"] = $user["CREATION_TIME"];
            echo json_encode($response);
        } else {
            // user not found
            // echo json with error = 1
            $response["error"] = 1;
            $response["error_msg"] = "Incorrect email or password!";
            echo json_encode($response);
        }
    } else if ($tag == 'register') {
        // Request type is Register new user
        $name = $_POST['username'];
        $email = $_POST['email'];
        $password = $_POST['password'];

        // check if user is already existed
        if ($db->doesEmailExists($email)) {
            // email already in use - error response
            $response["error"] = 3;
            $response["error_msg"] = "Email is already in use";
            echo json_encode($response);
        } else if ($db->doesUsernameExists($username)) {
            // user is already exists - error response
            $response["error"] = 2;
            $response["error_msg"] = "User already exists";
            echo json_encode($response);
        } else {
            // store user
            $user = $db->storeUser($name, $email, $password);
            if ($user) {
                // user stored successfully
                $response["success"] = 1;
	            $response["user"]["id"] = $user["ID"];
	            $response["user"]["name"] = $user["USERNAME"];
	            $response["user"]["email"] = $user["EMAIL"];
	            $response["user"]["creation_time"] = $user["CREATION_TIME"];
                echo json_encode($response);
            } else {
                // user failed to store
                $response["error"] = 1;
                $response["error_msg"] = "Error occured during registration";
        		$response["mysql_error"] = mysql_error();
                echo json_encode($response);
            }
        }
    } else if ($tag == 'insert' || $tag == 'update') {
        // Request type is Register new user
        $query = $_POST['query'];
        
        $result = $db->executeQuery($query);
        
        if($result > -1) {
        	$response["success"] = 1;
        	$response["id"] = $result;
            echo json_encode($response);
        } else {
        	$response["error"] = 1;
        	$response["error_message"] = "Error occured during operation";
        	$response["mysql_error"] = mysql_error();
            echo json_encode($response);
        }
    } else if ($tag == 'select') {
        // Request type is Register new user
        $tableName = $_POST['tableName'];
        $whereClause = $_POST['whereClause'];
        
        $result = $db->genericItemSelector($tableName, $whereClause);
        
        if(isset($result) && count($result) > 0) {
        	$response["success"] = 1;
        	
        	$i = 0;
        	foreach($result as $user) {
				foreach($user as $key => $value) {
					$response["objects"][$i][$key] = $value;
				}
				$i++;
			}
            echo json_encode($response);
        } else {
        	$response["error"] = 1;
        	$response["error_message"] = "Error occured during selection";
        	$response["mysql_error"] = mysql_error();
            echo json_encode($response);
        }
    } else {
        echo "Invalid Request";
    }
} else {
    echo "Access Denied";
}
?>
