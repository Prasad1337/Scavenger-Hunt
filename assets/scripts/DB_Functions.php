<?php

class DB_Functions {

    private $db;
    
    //put your code here
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }

    // destructor
    function __destruct() {
        
    }
    
    public function genericItemSelector($tablename, $whereClause) {
    	$result = mysql_query("SELECT * FROM $tablename " . (empty($whereClause) ? "" : " WHERE " . $whereClause));
    	
        $i = 0;
        $objects;
        while ($row = mysql_fetch_assoc($result)) {
		    foreach($row as $key => $value) {
		        $objects[$i][$key] = $value;
		     }
		     $i++;
		}
		
		return $objects;
    }
    
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
        $result = mysql_query("INSERT INTO USERS(USERNAME, EMAIL, PASSWORD) VALUES('$name', '$email', '$password')");
        // check for successful store
        if ($result) {
            // get user details 
            $id = mysql_insert_id(); // last inserted id
            $result = mysql_query("SELECT * FROM USERS WHERE ID = $id");
            // return user details
            return mysql_fetch_array($result);
        } else {
            return false;
        }
    }
    
    public function executeQuery($query, $tag) {
    	$result = mysql_query($query);
    	
    	if($result) {
    		if($tag == 'insert') {
    			return mysql_insert_id();
    		} else {
    			return 0;
    		}
    	} else {
    		return -1;
    	}
    }
    
    /**
     * Get user by email and password
     */
    public function getUserByUsernameAndPassword($username, $password) {
        $result = mysql_query("SELECT * FROM USERS WHERE USERNAME = '$username'");
        // check for result 
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
            if($result["PASSWORD"] == $password) {
            	return $result;
            }
        } else {
            // user not found
            return false;
        }
    }

    /**
     * Check user exists or not
     */
    public function doesUsernameExists($username) {
        $result = mysql_query("SELECT USERNAME from USERS WHERE USERNAME = '$username'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
    }

    /**
     * Check user exists or not
     */
    public function doesEmailExists($email) {
        $result = mysql_query("SELECT EMAIL from USERS WHERE EMAIL = '$email'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
    }

    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

}

?>
