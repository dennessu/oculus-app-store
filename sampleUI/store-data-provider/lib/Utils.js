/*
 Fill original object use target object
 @type: full, OneWay
 @return: original object
 */
exports.FillObject = function (original, target, type) {
    for (var p in original) {
        var p_type = typeof(original[p]);

        if (p_type != "function") {
            if (p_type == "object") {
                if (typeof(target[p]) != "undefined") {
                    original[p] = this.FillObject(original[p], target[p], type);
                }
            } else {
                if (typeof(target[p]) != "undefined") {
                    original[p] = target[p];
                }
            }
        }
    }

    if (type.toLowerCase() == "full") {
        // Append new property
        for (var p in target) {
            var p_type = typeof(target[p]);

            if (p_type != "function") {
                if (typeof(original[p]) != "undefined") continue;
                original[p] = target[p];
            }
        }
    }

    return original;
};