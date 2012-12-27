$(function(){
App.views.BasicView = Backbone.View.extend({
 
  //el: '#root',
  
  events: {
    //'click #create-form' : 'createForm'
  },

  initialize: function() {  
    console.log('init view ' + this.model.get('id') );

    this.insideViews = this.model.get('inside').map(this.generateInside, this);

  },

  render: function() {
    
    var el  = this.$el;
    var mo  = this.model;

    console.log('rendering view ' + mo.get('id') );
    
    var pos  = mo.get('pos'  ).split(' ');
    var size = mo.get('shape').split(' '); 


    var cssFeatures = {
      'position'          :  'absolute'        ,
      'left'              :  pos[0]  + 'px'    ,
      'top'               :  pos[1]  + 'px'    ,
      'width'             :  size[0] + 'px'    ,
      'height'            :  size[1] + 'px'    ,
      'border'            :  mo.get('border')  ,
      'background-color'  :  mo.get('color')   ,  
    };

    var html = mo.get('html') ;


    if( mo.get('type') == 'watch' ){
      var target = App.all.get( mo.get('target') ) ;
      if( target ){
        html = JSON.stringify( target );
      }
    }

    el.append( html );

    _.chain(cssFeatures).pairs().map(function(p){ el.css(p[0],p[1]) }) ;

    if( mo.get('draggable') ){ el.draggable({start: function( event, ui ) {}} ) }


    // inside

    _.each( this.insideViews , function( v ){
      v.render();
    });
  

    
    return this ;
  },

  generateInside: function( m ) {
    var newElemId = m.get('id') ;
    this.$el.append(  '<div id="'+newElemId+'"></div>' );
    return new App.views.BasicView({ model: m , el : '#'+newElemId });
  },

  
});
});
