var rest = require('restler');

exports.getOffers = function(req, res){
    rest.get("http://localhost:8080/rest/offers").on('complete', function(result) {
        res.send(result.results);
    });
};