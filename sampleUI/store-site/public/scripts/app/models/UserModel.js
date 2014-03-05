define([
    'ember-data-load'
], function(DS){

    return DS.Model.extend({
        username: DS.attr('string'),
        email: DS.attr('string')
    });

});