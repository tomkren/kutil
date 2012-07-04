<?php

function main(){

    $get  = $_GET['get'];
    $set  = $_GET['set'];
    $val  = $_GET['val'];
    $set2 = $_POST['set'];
    $val2 = $_POST['val'];

    connect();

    if( $get != "" ){
        get( $get );
    } else if( $set != "" ){
        set( $set, $val );
    } else if( $set2 != "" ){
        set( $set2 , $val2 );
    } else {
        echo "Welcome to kutil-server !";
    }


}


function get( $id ){
    echo getFromDB( $id );
}

function set( $id , $val ){
    setToDB($id, $val);
    echo "SET!";
}

function connect() {
    $host     = "localhost";
    $user     = "czkutil";
    $password = "20hlav";
    $db       = "czkutil";
      
    $spojeni = mysql_connect("$host", "$user", "$password")
          or die ("Nemohu se pripojit k databazovemu serveru.");

    $pripojit = mysql_select_db("$db")
          or die ("Nemohu vybrat databazi.");
}

function getFromDB( $id ) {
    $arr = mysql_fetch_array( mysql_query("SELECT val FROM webslots WHERE id = '$id' LIMIT 1" ));
    return $arr[ 0 ];
}

function setToDB( $id , $val ) {
    mysql_query("SELECT id FROM webslots WHERE id = '$id' LIMIT 1");
    if( mysql_affected_rows() == 0 ){
        mysql_query("INSERT INTO webslots SET id = '$id' , val = '$val' ");
    } else {
        mysql_query("UPDATE webslots SET val = '$val' WHERE id = '$id' LIMIT 1" );
    }
}



main();

?>