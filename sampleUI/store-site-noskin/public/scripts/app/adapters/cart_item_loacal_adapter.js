define([
  'ember-data-load',
    'local-storage',
  'app-setting'
], function(DS, setting){

  return DS.LSAdapter.extend({
    namespace: 'store-storage'
  });

});