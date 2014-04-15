var Utils = {

    /*
        Deep Clone
     */
    Clone: function (sObj) {
        if (typeof sObj !== "object") return sObj;

        var s = {};
        if (sObj.constructor == Array) s = [];

        for (var i in sObj) s[i] = Utils.Clone(sObj[i]);

        return s;
    },

    /*
     Fill original object use target object
     @type: 0:full(if property not exists, add a new property), 1:ChildFull(child properties is full fill), 2:OneWay(fill original from target just have properties)
     @return: original object(reference).
     */
    FillObject: function (original, target, type) {

        if (original == undefined || original == null) {
            original = Utils.Clone(target);
            return original;
        }

        // fill exists properties
        for (var p in original) {
            var p_type = typeof(original[p]);

            if (p_type == "object") {
                if (typeof(target[p]) != "undefined") {
                    if (type == 1) {
                        original[p] = Utils.Clone(target[p]);
                    } else {
                        original[p] = Utils.FillObject(original[p], target[p], type);
                    }
                }
            } else {
                // base type、Array or function
                if (typeof(target[p]) != "undefined") {
                    original[p] = Utils.Clone(target[p]);
                }
            }
        }

        // add new properties
        if (type == 0) {
            for (var p in target) {
                var p_type = typeof(target[p]);
                if (typeof(original[p]) != "undefined") continue;
                original[p] = Utils.Clone(target[p]);
            }
        }

        return original;
    }
};

module.exports = Utils;