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
                    } else {
                        console.log("Not blog");
                    }
                }

                $scope.readMore = function (post) {
                    $location.path('/blog/' + post.title.replace(/ /g, '-'));
                };

            },

            templateUrl: "/app/directives/blog/blogDirectiveView.html"
        }
    });