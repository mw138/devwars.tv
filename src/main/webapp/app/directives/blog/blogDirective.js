angular.module('app.blogDirective', [])
    .directive("blog", function () {
        return {
            restrict: "E",

            scope: {
                "post" : "="
            },

            controller: function ($scope, $location) {

                $scope.shouldShowTags = function (blog) {
                    if(blog) {
                        return blog.tags.length > 0;
                    }
                };

                $scope.readMore = function (post) {
                    $location.path('/blog/' + post.title.substring(0, 30).replace(/ /g, '-'));
                };

            },

            templateUrl: "/app/directives/blog/blogDirectiveView.html"
        }
    });