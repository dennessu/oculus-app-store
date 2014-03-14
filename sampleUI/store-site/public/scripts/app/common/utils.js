var Utils = {

    GetViews: function (templateObj) {
        if (templateObj == undefined || templateObj == null) {
            throw "The template not exists!";
        }

        if (Ember.TEMPLATES[templateObj.name] == undefined || Ember.TEMPLATES[templateObj.name] == null) {
            console.log("Request Template: ", templateObj.name);

            $.ajax({
                url: templateObj.url,
                async: false,
                success: function (data, textStatus, jqXHR) {
                    Ember.TEMPLATES[templateObj.name] = Ember.Handlebars.compile(data);
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    throw errorThrown;
                }
            });
        }
        return Ember.TEMPLATES[templateObj.name];
    }
};