var Store = require('./store');
var Identity = require('./identity');
var Payment = require('./payment');


module.exports = function(app){

    /*
        1.Store
        2.Login
        3.Checkout
        4.Account
    */

    app.get('/', Store.Index);

    app.get('/detail',Store.ProductDetail);
    app.get('/cart',Store.ShoppingCart);

    app.get('/login', Identity.Login);
    app.get('/login/TFA', Identity.LoginSuccess);

    app.get('/register/Notification', Identity.RegisterNotification);
    app.get('/register/Verification/Notification', Identity.RegisterVerificationNotification);

    app.get('/payment/Entry', Payment.Entry);
    app.get('/payment/Create', Payment.Create);
};