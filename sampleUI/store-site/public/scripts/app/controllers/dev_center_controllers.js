var DevCenterControllers = {
    Index: Ember.ObjectController.extend({
        errMessage: null,
        content: {
            isDeveloper: false
        },
        actions: {
            DialogYes: function () {
                var _self = this;

                var provider = new EntitlementProvider();
                provider.Post(Utils.GenerateRequestModel(null), function (result) {
                    if (result.data.status == 200) {
                        _self.set("errMessage", null);
                        $("#ConfirmDialog").hide();
                    } else {
                        _self.set("errMessage", "Please try again later!");
                    }
                });
            },

            DialogNo: function () {
                this.transitionToRoute("index");
            }
        }
    })
};