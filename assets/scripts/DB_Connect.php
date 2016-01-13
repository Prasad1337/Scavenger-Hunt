<?php
class DB_Connect {
	

    // constructor
    function __construct() {
        
    }

    // destructor
    function __destruct() {
        // $this->close();
    }

    // Connecting to database
    public function connect() {
	    $server = "localhost";
		$user   = "aerogel";
		$pw     = "UTtUvkZGP!TO";
		$db		= "scavenger_hunt";
	
		$link = mysql_connect($server, $user, $pw);
		
        // selecting database
        mysql_select_db($db);

        // return database handler
        return $link;
    }

    // Closing database connection
    public function close() {
        mysql_close();
    }

}

?>
