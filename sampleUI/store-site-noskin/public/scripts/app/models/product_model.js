define([
  'ember-data-load'
], function(DS){

  return DS.Model.extend({
    name: DS.attr("string"),
    picture_url: DS.attr("string"),
    price: DS.attr('number')
  });

});