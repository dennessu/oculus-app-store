
// target defined fill to original
exports.MoreFilling = function(original, target){

  for(var p in original){
    var p_type = typeof(original[p]);

    if(p_type != "function"){
      if(p_type == "object"){
        if(typeof(target[p]) != "undefined"){
          original[p] = this.MoreFilling(original[p], target[p]);
        }
      }else{
        if(typeof(target[p]) != "undefined"){
          original[p] = target[p];
        }
      }
    }
  }

  // Append new property
  for(var p in target){
    var p_type = typeof(target[p]);

    if(p_type != "function"){
      if(typeof(original[p]) != "undefined") continue;
      original[p] = target[p];
    }
  }

  return original;
}