App.models.Basic = Backbone.Model.extend({


  //idAttribute: '_id',

  defaults: {
    type      : 'basic',
    pos       : '10 10',
    shape     : '16 16', // obecne 'prefix arg1 arg2 ...'
    html      : '',
    border    : '',
    color     : 'yellow',
    draggable : true ,
    inside    : [],
  },

  initialize : function(){
    console.log( 'init model ' + this.get('id') );

    App.all.add( this );

    var inModelsArr = _.map( this.get('inside') , function( ob ){
      return new App.models.Basic( ob );
    });

    var newInside = new App.collections.Basics( inModelsArr ) ;

    this.set('inside' , newInside );

  },

  // @TODO implement attribute validation
  validate: function(attributes) {
  }

});

