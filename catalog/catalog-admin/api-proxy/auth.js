var rest = require('restler');
var config = require('../config');
var request = require('request');

exports.Callback = function(req, res){
    res.send("<html><body onload='redirect()'><script>function redirect(){window.location='http://localhost:3000/login/?'+ window.location.hash.substr(1)}</script></body></html>")
    /*console.log(req.query);
    if (!(req.query && req.query.id_token && req.query.access_token)) {
        console.log("Cannot get id_token and access_token!");
        console.log(req.url);
        res.send("<html><body>[Login Failed] Cannot get id_token and access_token!</body></html>");
        return;
    }

    rest.get(config.oauth_url + "tokeninfo?access_token=" + data.access_token)
        .on('complete', function(result) {
            console.log(result);
            res.cookie('user_id', result.sub.id, { maxAge: 900000, httpOnly: false });
            res.cookie('access_token', req.query.access_token, { maxAge: 900000, httpOnly: false });
            res.cookie('id_token', req.query.id_token, { maxAge: 900000, httpOnly: false })
            //res.send(result);


            var options = {
                url: 'http://10.0.1.137:8082/oauth2/userinfo',
                headers: {
                    'Authorization': 'Bearer ' + req.query.access_token
                }
            };

            function callback(error, response, body) {
                if (!error && response.statusCode == 200) {
                    var info = JSON.parse(body);
                    console.log(info.stargazers_count + " Stars");
                    console.log(info.forks_count + " Forks");

                    res.redirect("/#/login");

                } else {
                    console.log('error occurred when getting userinfo');
                    console.log(error);
                    console.log(response.statusCode);
                }
            }

            request(options, callback);

            //res.redirect("/#/login");
        });
     */

    //res.send("<html><body>hello</body></html>");
};

exports.Login = function(req, res){
    if (!(req.query && req.query.id_token && req.query.access_token)) {
        console.log("Cannot get id_token and access_token!");
        console.log(req.url);
        res.send("<html><body>[Login Failed] Cannot get id_token and access_token!</body></html>");
        return;
    }

    rest.get(config.oauth_url + "tokeninfo?access_token=" + req.query.access_token)
        .on('complete', function(result) {
            console.log(result);
            res.cookie('user_id', result.sub.id, { maxAge: 900000, httpOnly: false });
            res.cookie('access_token', req.query.access_token, { maxAge: 900000, httpOnly: false });
            res.cookie('id_token', req.query.id_token, { maxAge: 900000, httpOnly: false })
            //res.send(result);


            var options = {
                url: config.oauth_url + 'userinfo',
                headers: {
                    'Authorization': 'Bearer ' + req.query.access_token
                }
            };

            function callback(error, response, body) {
                if (!error && response.statusCode == 200) {
                    var info = JSON.parse(body);
                    console.log(info.email + " Email");
                    res.cookie('email', info.email, { maxAge: 900000, httpOnly: false })
                    res.redirect("/#/offers");

                } else {
                    console.log('error occurred when getting userinfo');
                    console.log(error);
                    console.log(response.statusCode);
                    res.send('error occurred when getting userinfo');
                }
            }

            request(options, callback);

            //res.redirect("/#/login");
        });

};

exports.Login2 = function(req, res){
    console.log(req.query);
    if (!(req.query && req.query.code)) {
        console.log(req.query.hi);
        console.log(req.query.code);
        console.log(req.query);
        console.log(req.url);
        //console.log(req);
        console.log("Cannot get code!");
        res.send("<html><body>[Login Failed] Cannot get code!</body></html>");
        return;
    }

    rest.post(config.oauth_url + "token", {
       data: {
           client_id: 'catalog-admin',
           client_secret: 'secret',
           redirect_uri: 'http://localhost:3000/auth/',
           grant_type: 'authorization_code',
           code: req.query.code
       }
    }).on('complete', function(data){
        console.log(data);

        rest.get(config.oauth_url + "tokeninfo?access_token=" + data.access_token)
            .on('complete', function(result) {
                console.log(result);
                res.cookie('user_id', result.sub.id, { maxAge: 900000, httpOnly: false });
                res.cookie('access_token', data.access_token, { maxAge: 900000, httpOnly: false });
                //res.send(result);
                res.redirect("/#/login");
            });
    });

    //res.send("<html><body>hello</body></html>");
};
