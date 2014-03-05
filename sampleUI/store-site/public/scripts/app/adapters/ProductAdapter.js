define([
  'ember-data-load',
  'app-setting'
], function(DS, setting){

  return DS.RESTAdapter.extend({

    host: setting.ProductAdapterHost,
    namespace: setting.ProductAdapterNamespace
  });

});