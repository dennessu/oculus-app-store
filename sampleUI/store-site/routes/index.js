var Store = require('./store');
var Identity = require('./identity');


module.exports = function(app){

    app.get('/', Store.Index);

    app.get('/detail',Store.ProductDetail);
};