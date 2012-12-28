$(function(){ 

	//random testing
  //var c=$('#myCanvas')[0];
  //var ctx=c.getContext("2d");
  //ctx.fillStyle="#FF0000";
  //ctx.fillRect(0,0,150,75);

  App.all = new App.collections.Basics( ) ;

  App.root = new App.models.Basic({
    id        : '_1',
  	pos       : '100 50',
  	shape     : '500 600',
  	html      : '<b>Hell</b>o world!',
  	border    : 'solid 1px cyan' ,
  	color     : 'magenta',
  	draggable : true ,
    inside    : [
      { type: 'watch', 
        target: '_2',
        id: '_2',  
        pos: '100 200',
        shape: '250 300' },
      { id: '_3' , 
        pos:'10 30', 
        html : 'cde' },

    ],
  });

  App.rootView = new App.views.BasicView( { model : App.root , el : '#root' } );
  
  //App.router = new App.Router;
  //Backbone.history.start();

	App.rootView.render();


});