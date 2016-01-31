angular.module("app.dashnav", [])
    .directive("dashnav", function () {
        return {
            restrict: "E",
            templateUrl: "/app/directives/dashnavDirective/dashnavView.html",

            controller: function ($scope) {
                $scope.goToProfile = function (profile) {
                    location.href = "/profile?username=" + profile;
                }
            }
        };
    })
