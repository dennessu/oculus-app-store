/*
 Fill original object use target object
 @type: 0:full, 1:ChildFull, 2:OneWay
 @return: original object
 */
exports.FillObject = function(original, target, type){
    if(type == 0 && (original == undefined || original == null)) return target;

    for(var p in original){
        var p_type = typeof(original[p]);

        if(p_type != "function"){
            if(p_type == "object"){
                if(typeof(target[p]) != "undefined" && target[p] != null){
                    if(type == 1){
                        original[p] = this.FillObject(original[p], target[p], 0);
                    }else{
                        original[p] = this.FillObject(original[p], target[p], type);
                    }
                }
            }else{
                if(typeof(target[p]) != "undefined" && target[p] != null){
                    original[p] = target[p];
                }
            }
        }
    }

    if (type == 0) {
        // Append new property
        for (var p in target) {
            var p_type = typeof(target[p]);

            if (p_type != "function") {
                if (typeof(original[p]) != "undefined" && original[p] != null) continue;
                original[p] = target[p];
            }
        }
    }

    return original;
};