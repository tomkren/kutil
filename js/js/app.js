$(function(){ 

	//random testing
  //var c=$('#myCanvas')[0];
  //var ctx=c.getContext("2d");
  //ctx.fillStyle="#FF0000";
  //ctx.fillRect(0,0,150,75);

  App.root = new App.models.Basic({
  	pos    : '100 200',
  	shape  : '500 100',
  	html   : '<b>Hell</b>o world!',
  	border : 'solid 1px yellow' ,
  	color  : 'gray',
  	draggable : true ,
  });

  App.rootView = new App.views.BasicView( { model : App.root } );
  
  //App.router = new App.Router;
  //Backbone.history.start();

	App.rootView.render();


});