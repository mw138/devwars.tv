angular.module("app.blog", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('blogList', {
                    url: '/blog',
                    templateUrl: '/app/pages/blogList/blogListView.html',
                    controller: "BlogListController"
                });

        }])
    .controller("BlogListController", ["$scope", "BlogService", "$mdDialog", "ToastService", "AuthService", "$anchorScroll", "$sce", function ($scope, BlogService, $mdDialog, ToastService, AuthService, $anchorScroll, $sce) {
        $scope.posts = [];

        $scope.AuthService = AuthService;

        $scope.updatePosts = function () {
            BlogService.allPosts(null, null, null, function (success) {
                $scope.posts = success.data;
            }, function (error) {
                console.log(error);
            });
        }

        $scope.newPost = function ($event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/addBlogPostDialog/addBlogPostDialogView.html",
                controller: "AddBlogPostDialogController",
                targetEvent: $event
            })
                .then(function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Successfully published", success.title);
                    $scope.updatePosts();
                }, function (error) {
                    //Otherwise means they just clicked cancel
                    if(error) {
                        var messages = error.data.map(function (error) {
                           return error.defaultMessage;
                        }).join('\n');

                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", messages);
                    }
                })
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
                    $scope.updatePosts();
                }, angular.noop)
        }

        $scope.deletePost = function (post) {
            BlogService.deleteBlog(post.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Successfully deleted post", success.data.title);
                $scope.updatePosts();
            }, function () {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not delete post");
            })
        }

        $scope.updatePosts();
    }])