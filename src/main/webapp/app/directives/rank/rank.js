angular.module("app.rank", [])
    .directive("rank", function () {
        return {
            restrict: "A",

            scope: {
               rank: "="
            },

            controller: function($scope) {

            },

            templateUrl: "/app/directives/rank/rankView.html"
        }
    })