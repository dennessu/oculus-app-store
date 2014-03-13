var Store = require('./store');
var Identity = require('./identity');
var Payment = require('./payment');
var Account = require('./account');

module.exports = function(app){

    /*
        Application
        1.Store
        2.Auth
    */

    // Application
    app.get('/', Store.Index);
    app.get('/Auth', Identity.Index);

    // Template

    // Rest

    app.get('/detail',Store.ProductDetail);
    app.get('/cart',Store.ShoppingCart);
    app.get('/order',Store.OrderSummary);
    app.get('/purchase/Notification',Store.PurchaseNotification);

    app.get('/login', Identity.Login);
    app.get('/login/TFA', Identity.LoginSuccess);

    app.get('/register/Notification', Identity.RegisterNotification);
    app.get('/register/Verification/Notification', Identity.RegisterVerificationNotification);

    app.get('/account', Account.Settings);
    app.get('/account/EditSetting', Account.EditSettings);
    app.get('/account/EditPassword', Account.EditPassword);
    app.get('/account/Profile', Account.Profile);
    app.get('/account/EditProfile', Account.EditProfile);
    app.get('/account/Payment', Account.Payment);

    app.get('/payment/Entry', Payment.Entry);
    app.get('/payment/Create', Payment.Create);
    app.get('/payment/Address', Payment.ShippingAddress);
    app.get('/payment/SelectePaymentMethod', Payment.SelectePaymentMethod);
};