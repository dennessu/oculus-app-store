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
            case Ember.App.ShippingInfo:
                result = Transition.ShippingInfo(data);
                break;
            case Ember.App.CreditCard:
                result = Transition.CreditCard(data);
                break;
            case Ember.App.Profile:
                result = Transition.Profile(data);
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
        if (data["results"] != undefined && data["results"] instanceof Array && typeof(data["self"]) == "undefined") {
            // get offers
            var offers = data["results"];
            var resultList = new Array();
            for (var i = 0; i < offers.length; ++i) {
                var item = offers[i];

                try {
                    resultList.push({
                        "id": item.self.id,
                        "name": item.name,
                        "price": item.prices.US.amount,
                        "picture": item.properties.mainImage,
                        "description": item.properties.longDescription
                    });
                }catch(e){}
            }

            result = {"Products": resultList};
        } else {
            // get offers by id
            var item = data;

            try {
                result = {"Product": {
                    "id": item.self.id,
                    "name": item.name,
                    "price": item.prices.US.amount,
                    "picture": item.properties.mainImage,
                    "description": item.localeProperties.DEFAULT.description
                }};
            }catch(e){}
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
    },

    ShippingInfo: function(data){
        if(data["results"] != undefined && data["results"] instanceof Array && typeof(data["self"]) == "undefined"){
            var result = data["results"];
            for(var i = 0; i < data.length; ++i){
                var item = data[i];
                result.push({
                    id: item.self.id,
                    street: item.street,
                    city: item.city,
                    state: item.state,
                    postalCode: item.postalCode,
                    country: item.country,
                    firstName: item.firstName,
                    lastName: item.lastName,
                    phoneNumber: item.phoneNumber
                });
            }
            return {"ShippingInfos": result};
        }else{
            var item = data;
            return {
                id: item.self.id,
                street: item.street,
                city: item.city,
                state: item.state,
                postalCode: item.postalCode,
                country: item.country,
                firstName: item.firstName,
                lastName: item.lastName,
                phoneNumber: item.phoneNumber
            };
        }
    },

    CreditCard: function(data){
        var result = null;
        if(data["results"] != undefined && data["results"] instanceof Array && typeof(data["self"]) == "undefined"){
            var payments = data["results"];
            var paymentArray = new Array();
            for(var i = 0; i < payments.length; ++i){
                var item = payments[i];
                paymentArray.push({
                    id: item.self.id,
                    accountName: item.accountName,
                    accountNum: item.accountNum,
                    isValidated: item.isValidated,
                    isDefault: item.isDefault,
                    expireDate: item.creditCardRequest.expireDate,
                    encryptedCvmCode: "",
                    addressLine1: item.address.addressLine1,
                    city: item.address.city,
                    state: item.address.state,
                    country: item.address.country,
                    postalCode: item.address.postalCode,
                    phoneType: item.phone.type,
                    phoneNumber: item.phone.number
                });
            }

            result = {"CreditCards": paymentArray};
        }else{
            var item = data;
            result = {
                id: item.self.id,
                accountName: item.accountName,
                accountNum: item.accountNum,
                isValidated: item.isValidated,
                isDefault: item.isDefault,
                expireDate: item.creditCardRequest.expireDate,
                encryptedCvmCode: "",
                addressLine1: item.address.addressLine1,
                city: item.address.city,
                state: item.address.state,
                country: item.address.country,
                postalCode: item.address.postalCode,
                phoneType: item.phone.type,
                phoneNumber: item.phone.number
            };
        }

        return result;
    },

    Profile: function(data){
        var result = null;

        var item = data;
        result = {
            id: item.self.id,
            type: item.type,
            region: item.region,
            firstName: item.firstName,
            middleName: item.middleName,
            lastName: item.lastName,
            dateOfBirth: item.dateOfBirth,
            locale: item.locale
        };

        return result;
    }
};
