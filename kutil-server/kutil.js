include( 'std.js' );

var canvas;


function include( src ){
    var headID = document.getElementsByTagName("head")[0];
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = src;
    headID.appendChild(newScript);
}

window.onload = function(){

    console.log("Start of function kutil.js main() ");

    document.title = "kutil";

    canvas = document.createElement("canvas");
    document.body.appendChild(canvas);

    resetCanvas(canvas);

    var kutilElements = document.getElementsByClassName("kutil");

    console.log( 'len: ' + kutilElements.length );

    for( var i = 0 ; i < kutilElements.length ; i++ ){
        console.log(kutilElements[i].innerHTML );
        //kutilElements[i].parentNode.removeChild( kutilElements[i] );
    }
}

window.onresize = function(){
    resetCanvas( canvas );
}



function resetCanvas( canvas ){
    var border       = 5;
    var canvasWidth  = window.innerWidth   - 2*border ;
    var canvasHeight = window.innerHeight  - 2*border ;

    canvas.width          = canvasWidth;
    canvas.height         = canvasHeight;
    canvas.style.position = "absolute";
    canvas.style.top      = border + "px";
    canvas.style.left     = border + "px";

    var context = canvas.getContext("2d");

    context.strokeRect(0.5, 0.5, canvas.width-1, canvas.height-1 );
    context.moveTo(0, 0);
    context.lineTo(canvas.width, canvas.height);
    context.stroke();

    context.strokeRect(100.5, 100.5, 200, 200);
}




