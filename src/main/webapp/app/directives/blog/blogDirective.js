angular.module('app.blogDirective', [])
    .directive("blog", function () {
        return {
            restrict: "E",

            scope: {
                "post" : "="
            },

            templateUrl: "/app/directives/blog/blogDirectiveView.html"
        }
    });