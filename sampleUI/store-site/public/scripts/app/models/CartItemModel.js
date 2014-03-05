define([
    'ember-data-load'
], function(DS){

    return DS.Model.extend({
        product_id: DS.attr('string'),
        unit_price: DS.attr('number'),
        count: DS.attr('number'),
        status: DS.attr('boolean')
    });

});