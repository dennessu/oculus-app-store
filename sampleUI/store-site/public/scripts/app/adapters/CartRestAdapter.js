define([
  'ember-data-load',
  'app-setting'
], function(DS, setting){

  return DS.Store.extend({
      adapter: DS.RESTAdapter.extend({

          host: setting.CartAdapterHost,
          namespace: setting.CartAdapterNamespace
      })
  });

});