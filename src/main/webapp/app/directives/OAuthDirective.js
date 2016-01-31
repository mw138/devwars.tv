angular.module("app.OAuthDirective", [])
    .directive("oauth", function () {
        return {
            restrict: "A",

            controller: function ($scope, $element, $attrs) {
                $element.bind("click", function () {

                    if ($attrs.provider) {
                        location.href = "/v1/oauth/" + $attrs.provider;
                    }

                });
            }
        }
    })
