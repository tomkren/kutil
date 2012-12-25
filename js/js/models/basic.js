App.models.Basic = Backbone.Model.extend({


  //idAttribute: '_id',

  defaults: {
    type    : 'basic',
    pos     : '0 0',
    shape   : '16 16', // obecne 'prefix arg1 arg2 ...'
    html    : '',
    border  : '',
    color   : 'black',
  },

  initialize : function(){
    //console.log( 'Inicializuji basic model.' );
  },

  // @TODO implement attribute validation
  validate: function(attributes) {
  }

});

