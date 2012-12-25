$(function(){

App.views.BasicView = Backbone.View.extend({
  el: '#root',
  
  events: {
    //'click #create-form' : 'createForm'
  },

  initialize: function() {    
    //this.$tabBody = $('#form-tab-body');

    //App.forms.on( 'reset'  , this.render , this );


  },

  render: function() {
    console.log('rendering basic thing..');
    
    var el  = this.$el;
    var mo  = this.model;
    
    var pos  = mo.get('pos'  ).split(' ');
    var size = mo.get('shape').split(' '); 

    el.html( mo.get('html') );

    el.css( 'position'         , 'absolute'        )
      .css( 'left'             , pos[0]  + 'px'    )
      .css( 'top'              , pos[1]  + 'px'    )
      .css( 'width'            , size[0] + 'px'    )
      .css( 'height'           , size[1] + 'px'    )
      .css( 'border'           , mo.get('border')  )
      .css( 'background-color' , mo.get('color')   )
      .draggable({start: function( event, ui ) {}} )
    ;


    hax = pos;



    //App.forms.each(this.renderRow, this);
  },

  //renderRow: function( m ) {
  //  var view = new App.views.FormRow({ model: m });
  //  this.$tabBody.append( view.render().el );
  //},

  
});

});
