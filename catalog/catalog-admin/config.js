config = {};
config.catalog_api_base_url = "http://localhost:8091/rest/";
config.attributes_url = config.catalog_api_base_url + "attributes/"
config.offers_url = config.catalog_api_base_url + "offers/"
config.items_url = config.catalog_api_base_url + "items/"

module.exports = config;