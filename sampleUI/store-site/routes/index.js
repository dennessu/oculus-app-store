var HomePage = require('./Home');
var ProductsPage = require('./Products');
var CartPage = require('./Cart');
var ShopPage = require('./Shop');

var AuthAction = require('./auth');
var API = require('./api');


module.exports = function (app) {
    app.get("/", HomePage);
    // ember template
    app.get("/Cart", CartPage.Cart);
    app.get("/Product", ProductsPage.Product);

    // UI
    app.get("/Shop", ShopPage);
    app.get("/Category", ProductsPage.Category);
    app.get("/Games", ProductsPage.Games);
    app.get("/Detail", ProductsPage.Detail);

    app.get("/auth/login", AuthAction.Login);
    app.get("/auth/logout", AuthAction.Logout);
    app.post('/auth/register', AuthAction.Register);

    app.get("/api/products/:id?", API.Products);
    app.get("/api/CartItems", API.GetCartItems);
    app.post("/api/AddCartItem", API.AddCartItem);
    app.post("/api/RemoveCartItem", API.RemoveCartItem);
}