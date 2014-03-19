var Store = require('./store');
var Identity = require('./identity');
var Payment = require('./payment');
var Account = require('./account');

var ClientConfigs = require('../configs/client_config');
var Template = require('./template');


module.exports = function(app){

    /*
        Application
        1.Store
        2.Auth
    */

    // Application
    app.get('/', function(req, res){
        res.render("index", {layout: false, title: "Store Demo"});
    });
    app.get('/Identity', function(req, res){
        res.render("identity/index", {layout: false, title: "Store Demo"});
    });

    // Config
    app.get('/config', function(req, res){
        res.json(ClientConfigs);
        res.end();
    });

    // Template
    app.get('/Template/Identity/Login', Template.Identity.Login);
    app.get('/Template/Identity/Captcha', Template.Identity.Captcha);
    app.get('/Template/Identity/TFA', Template.Identity.TFA);
    app.get('/Template/Identity/Register', Template.Identity.Register);
    app.get('/Template/Identity/PIN', Template.Identity.PIN);
    app.get('/Template/Identity/My', Template.Identity.My);

    app.get('/Template/Store/Index', Template.Store.Index);
    app.get('/Template/Store/Detail', Template.Store.Detail);

    // Redirect back handler
    app.get('/Callback/Login', function(req, res){});
    app.get('/Callback/Register', function(req, res){});

    // API Rest

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

    app.get('/test/products', function(req, res){
        res.json({"Products": [
            {
                "id": 111,
                "name": "3D Parking 1",
                "price": 9.99,
                "picture": "/images/P1.jpg",
                "description": ""
            }
            ,
            {
                "id": 222,
                "name": "3D Parking 1",
                "price": 9.99,
                "picture": "/images/P1.jpg",
                "description": ""
            }
        ]});
        res.end();
    });
};