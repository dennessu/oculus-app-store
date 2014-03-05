define([
  'ember-data-load',
  'app-setting'
], function(DS, setting){

  return DS.RESTAdapter.extend({
    bulkCommit: false,
    host: setting.AdapterHost,
    namespace: setting.AdapterNamespace
  });

});