angular.module('app.blogDirective', [])
    .directive("blog", function () {
        return {
            restrict: "E",

            scope: {
                "post" : "="
            },

            controller: function ($scope) {

                $scope.shouldShowTags = function (blog) {
                    if(blog) {
                        return blog.tags.length > 0;
                    } else {
                        console.log("Not blog");
                    }
                }

            },

            templateUrl: "/app/directives/blog/blogDirectiveView.html"
        }
    });