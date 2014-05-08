config = {};
config.catalog_api_base_url = "http://localhost:8080/v1/";
config.attributes_url = config.catalog_api_base_url + "attributes/"
config.offers_url = config.catalog_api_base_url + "offers/"
config.items_url = config.catalog_api_base_url + "items/"
config.price_tiers_url = config.catalog_api_base_url + "price-tiers/"
config.oauth_url = "http://api.oculusvr-demo.com:8081/rest/oauth2/";

module.exports = config;