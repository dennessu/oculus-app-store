define([
  'ember-data-load',
  'app-setting'
], function(DS, setting){

  return DS.RESTAdapter.extend({

    host: setting.IdentityAdapterHost,
    namespace: setting.IdentityAdapterNamespace
  });

});