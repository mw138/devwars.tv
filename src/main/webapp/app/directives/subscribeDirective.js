angular.module("app.subscribeDirective", [])
    .directive("subscribe", function () {
        return {
            restrict: "A",
            controller: function(AuthService, $element) {
                $element.bind("click", function () {
                    var link = "//devwars.us10.list-manage.com/subscribe/post?u=0719cdf11a95678dd04e5db33&id=02e4f24ca1";

                    if (AuthService.user && AuthService.user.email) {
                        link += "&EMAIL=" +AuthService.user.email;
                    }

                    window.open(link, "_blank");
                })
            }
        };
    })