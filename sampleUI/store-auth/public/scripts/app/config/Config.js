var require = {
  baseUrl:'/scripts/libs',
  /*urlArgs: "v=" +  (new Date()).getTime(),*/
  waitSeconds: 15,

  paths:{
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
    ember: 'ember/ember.min',
    'ember-data': 'ember/ember-data',
    'ember-load': 'ember/ember-load',
    'ember-data-load': 'ember/ember-data-load',
    'google-captcha': ['captcha/recaptcha_ajax', 'http://www.google.com/recaptcha/api/js/recaptcha_ajax'],

    // customer
    'app-setting': '../app/config/app_setting'
  },
  shim: {
    'jquery-metadata':['jquery'],
    bootstrap: ['jquery'],
    handlebars: ['jquery'],
    ember: ['jquery', 'bootstrap', 'handlebars'],
    'ember-data':['ember'],

    'jquery-validate': {
      deps: ['jquery', 'jquery-metadata'],
      exports: 'jQuery.fn.validate'
    },
    'jquery-cookie': {
      deps: ['jquery'],
      exports: 'jQuery.fn.cookie'
    },
    'google-captcha': {
      deps: ['jquery'],
      exports: 'Recaptcha'
    }
  },

  deps:['jquery', 'handlebars']
};

// Extend
String.prototype.format = function(args) {
  var result = this;
  if (arguments.length > 0) {
    if (arguments.length == 1 && typeof (args) == "object") {
      for (var key in args) {
        if(args[key]!=undefined){
          var reg = new RegExp("({" + key + "})", "g");
          result = result.replace(reg, args[key]);
        }
      }
    }
    else {
      for (var i = 0; i < arguments.length; i++) {
        if (arguments[i] != undefined) {
          var reg= new RegExp("({)" + i + "(})", "g");
          result = result.replace(reg, arguments[i]);
        }
      }
    }
  }
  return result;
}