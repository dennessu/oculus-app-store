var alphabetizeResources = function() {
    var mylist = $('#resources');
    var listitems = mylist.children('li.resource').get();
    listitems.sort(function(a, b) {
        return $(a).attr('id').toUpperCase().localeCompare($(b).attr('id').toUpperCase());
    })
    $.each(listitems, function(idx, itm) { mylist.append(itm); });

    // sort the APIs
    $('#resources li.resource .endpoints').each(function (idx, operations) {
        var listitems = $(operations).children("li.endpoint").get()

        var getOperationOrder = function(verb) {
            verb = verb.toLowerCase()
            if (verb == "post")   return 1
            if (verb == "get")    return 2
            if (verb == "put")    return 3
            if (verb == "delete") return 4
            return 5
        }

        var getKey = function(itm) {
            return $(itm).find(".operation .heading span.path a").text() + "."
                + getOperationOrder($(itm).find(".operation .heading span.http_method a").text())
        }

        listitems.sort(function(a, b) {
            return getKey(a).toLowerCase().localeCompare(getKey(b).toLowerCase())
        })

        $.each(listitems, function(idx, itm) { $(operations).append(itm) })
    })
};

var showSamples = function(swaggerApi) {
    var template =
      "<div class='sample'>\n" +
      "  <h5><a>Description</a></h5>\n" +
      "  <div class='sample-content' style='display: none'>\n" +
      "    <div class='block request_url'><h5>Request URL</h5><pre></pre></div>\n" +
      "    <div class='block request_headers'><h5>Request Headers</h5><pre></pre></div>\n" +
      "    <div class='block request_body undefined'><h5>Request Body</h5><pre class='json'><code>no content</code></pre></div>\n" +
      "    <div class='block response_code'><h5>Response Code</h5><pre></pre></div>\n" +
      "    <div class='block response_headers'><h5>Response Headers</h5><pre></pre></div>\n" +
      "    <div class='block response_body undefined'><h5>Response Body</h5><pre class='json'><code>no content</code></pre></div>\n" +
      "  </div>\n" +
      "</div>\n";
    var setContent = function(elem, text) {
        if (text) {
            elem.find("pre").text(text)
        } else {
            elem.hide()
        }
    }
    var setContentCode = function(elem, text) {
        if (text) {
            try {
                var jsonObj = $.parseJSON(text)
                elem.find("pre code").text(JSON.stringify(jsonObj, null, 2))
            } catch (e) {
                elem.find("pre code").text(text)
            }
        } else {
            elem.hide()
        }
    }
    var buildRequestUrl = function(operation, requestUrl) {
        return operation.method.toUpperCase() + " " + replaceMacros(operation, requestUrl)
    }
    var replaceMacros = function(operation, str) {
        if (str) {
            return str.replace('$baseUrl', operation.resource.basePath)
        }
        return str
    }
    $.each(swaggerApi.apisArray, function(idx, api) {
        $.each(api.operationsArray, function(idx, operation) {
            if (operation.samples) {
                var i = 0;
                var id = "#" + api.name + "_" + operation.nickname + "_content"
                $(id).append($("<h4>Samples</h4>"))
                $.each(operation.samples, function(idx, sample) {
                    var sampleContent = $(template)
                    $(sampleContent).find('h5 a').click(function() {
                        var elem = $(sampleContent).find('>.sample-content')
                        if (elem.is(":visible")) {
                            return elem.slideUp()
                        } else {
                            return elem.slideDown()
                        }
                    }).text("SAMPLE " + (++i) + ": " + sample.description)

                    setContent($(sampleContent).find('div.request_url'), buildRequestUrl(operation, sample.requestUrl))
                    setContent($(sampleContent).find('div.request_headers'), replaceMacros(operation, sample.requestHeaders))
                    setContentCode($(sampleContent).find('div.request_body'), replaceMacros(operation, sample.requestBody))
                    setContent($(sampleContent).find('div.response_code'), replaceMacros(operation, sample.responseCode))
                    setContent($(sampleContent).find('div.response_headers'), replaceMacros(operation, sample.responseHeaders))
                    setContentCode($(sampleContent).find('div.response_body'), replaceMacros(operation, sample.responseBody))

                    $(id).append(sampleContent)
                })
            }
        })
    })
}
