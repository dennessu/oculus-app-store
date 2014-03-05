var require = {
    baseUrl: '/scripts/libs',
    /*urlArgs: "v=" +  (new Date()).getTime(),*/
    waitSeconds: 15,

    paths: {
        app: '../app',

        // require plugins
        domReady: 'require/plugins/domReady',

        jquery: 'jquery/jquery-1.10.2.min',
        'jquery-metadata': 'jquery/jquery.metadata',
        'jquery-cookie': 'jquery/jquery.cookie',
        'jquery-validate': 'jquery/jquery.validate.min',
        'jquery-plugins-load': 'jquery/plugins-load',
        bootstrap: 'bootstrap/bootstrap.min',
        handlebars: 'handlebars/handlebars-v1.2.1',
        ember: 'ember/ember',
        'ember-data': 'ember/ember-data',
        'local-storage': 'ember/localstorage_adapter',
        'ember-load': 'ember/ember-load',
        'ember-data-load': 'ember/ember-data-load',

        // customer
        'app-setting': '../app/config/AppSetting'
    },
    shim: {
        'jquery-metadata': ['jquery'],
        bootstrap: ['jquery'],
        handlebars: ['jquery'],
        ember: ['jquery', 'bootstrap', 'handlebars'],
        'ember-data': ['ember'],
        'local-storage': ['ember', 'ember-data'],
        'jquery-cookie': ['jquery'],

        'jquery-validate': {
            deps: ['jquery', 'jquery-metadata'],
            exports: 'jQuery.fn.validate'
        },

        'app-setting':['ember']
    },

    deps: ['jquery', 'bootstrap', 'handlebars']
};

// Extend
String.prototype.format = function (args) {
    var result = this;
    if (arguments.length > 0) {
        if (arguments.length == 1 && typeof (args) == "object") {
            for (var key in args) {
                if (args[key] != undefined) {
                    var reg = new RegExp("({" + key + "})", "g");
                    result = result.replace(reg, args[key]);
                }
            }
        }
        else {
            for (var i = 0; i < arguments.length; i++) {
                if (arguments[i] != undefined) {
                    var reg = new RegExp("({)" + i + "(})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
    }
    return result;
}