var Transition = {
    Resolve: function (type, methodName, data) {
        if (type == undefined || methodName == undefined || data == undefined) {
            throw "Arguments Invalid!";
        }
        var result = null;

        switch (type) {
            case Ember.App.Product:
                result = Transition.OffersToProduct(data);
                break;
            case Ember.App.CartItem:
                result = Transition.CartItems(data);
                break;
            default :
                result = Transition.Normalize(data);
                break;
        }
        return result;
    },

    Normalize: function (data) {
        return data;
    },

    OffersToProduct: function (data) {
        var result = null;
        if (data["results"] != undefined) {
            // get offers

            var offers = data["results"];
            var resultList = new Array();
            for (var i = 0; i < offers.length; ++i) {
                var item = offers[i];

                resultList.push({
                    "id": item.self.id,
                    "name": item.name,
                    "price": item.prices.US.amount,
                    "picture": item.properties.mainImage,
                    "description": item.localeProperties.DEFAULT.description
                });
            }

            result = {"Products": resultList};
        } else {
            // get offers by id
            var item = data;

            result = {"Product": {
                "id": item.self.id,
                "name": item.name,
                "price": item.prices.US.amount,
                "picture": item.properties.mainImage,
                "description": item.localeProperties.DEFAULT.description
            }};
        }

        return result;
    },

    CartItems: function(data){
        var offers = data["offers"];
        var list = new Array();
        for (var i = 0; i < offers.length; ++i) {
            var item = offers[i];

            list.push({
                "id": i,
                "product_id": item.offer.id,
                "selected": item.selected,
                "qty": item.quantity
            });
        }

        return {"CartItems": list};
    }
};
