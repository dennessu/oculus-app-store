
exports.Index = function(req, res){
    res.render("store/index", {title: "Store Demo"});
};
exports.Detail = function(req, res){
    res.render("store/detail", {title: "Store Demo"});
}