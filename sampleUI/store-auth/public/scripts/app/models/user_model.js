define([
  'ember-data-load'
], function(DS){

  return DS.Model.extend({
    firstname: DS.attr("string"),
    lastname: DS.attr("string"),
    email: DS.attr('string'),
    password: DS.attr('string'),
    year: DS.attr('number'),
    month: DS.attr('number'),
    day: DS.attr('number')
  });

});