
var Utils = {

    GetViews: function(name){
        console.log("Get Template: ", name);

        if(name == undefined || name == null) return null;

        var viewUrl = "";
        for(var i = 0; i < AppConfig.Views.length; ++i){
            if(name.toLowerCase() === AppConfig.Views[i].name.toLowerCase()){
                viewUrl = AppConfig.Views[i].url;
                break;
            }
        }

        $.ajax({
            url: viewUrl,
            async: false,
            success: function (data) {
                Ember.TEMPLATES[name] = Ember.Handlebars.compile(data);
            }
        });
        return Ember.TEMPLATES[name];
    }

};