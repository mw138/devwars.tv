angular.module('app.blogDirective', [])
    .directive("blog", function (AuthService, BlogService, $mdDialog, ToastService) {
        return {
            restrict: "E",

            scope: {
                "post": "="
            },

            controller: function ($scope, $location) {

                $scope.location = window.location.href;
                $scope.AuthService = AuthService;

                $scope.shouldShowTags = function (blog) {
                    if (blog) {
                        return blog.tags.length > 0;
                    }
                };

                $scope.readMore = function (post) {
                    $location.path('/blog/' + post.title.substring(0, 30).replace(/ /g, '-'));
                };

                $scope.refreshPost = function (post) {
                    BlogService.getBlog(post.title, function (success) {
                        $scope.post = success.data;
                    }, angular.noop);
                };
                $scope.editPost = function (post, $event) {
                    $mdDialog.show({
                            templateUrl: "/app/components/dialogs/addBlogPostDialog/addBlogPostDialogView.html",
                            controller: "EditBlogPostDialogController",
                            targetEvent: $event,

                            locals: {
                                post: post
                            }
                        })
                        .then(function (success) {
                            ToastService.showDevwarsToast("fa-check-circle", "Success", "Edited post");
                            $scope.refreshPost($scope.post);
                        }, angular.noop)
                };

                $scope.deletePost = function (post) {
                    BlogService.deleteBlog(post.id, function (success) {
                        ToastService.showDevwarsToast("fa-check-circle", "Successfully deleted post", success.data.title);
                    }, function () {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not delete post");
                    })
                }
            },

            templateUrl: "/app/directives/blog/blogDirectiveView.html"
        }
    });
