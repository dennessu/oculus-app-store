var alphabetizeResources = function(){
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