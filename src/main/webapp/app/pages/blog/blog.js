angular.module("app.blogList", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('blog', {
                    url: '/blog/:title',
                    templateUrl: '/app/pages/blog/blogView.html',
                    controller: "BlogController",

                    resolve: {
                        posts: ['BlogService', function (BlogService) {
                            return BlogService.http.allPosts();
                        }]
                    }
                });
        }])
    .controller("BlogController", ['$scope', 'BlogService', '$mdDialog', 'ToastService', 'AuthService', '$anchorScroll', '$sce', 'posts', "$stateParams", function ($scope, BlogService, $mdDialog, ToastService, AuthService, $anchorScroll, $sce, posts, $stateParams) {
        $scope.posts = posts.data;

        $scope.AuthService = AuthService;

        if($stateParams.title)
        {
            BlogService.getBlog($stateParams.title, function (success) {
                $scope.post = success.data;
            }, angular.noop);
        } else $location.path('/');

        $scope.updatePosts = function () {
            BlogService.allPosts(null, null, null, function (success) {
                $scope.posts = success.data;
            }, function (error) {
                console.log(error);
            });
        };

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
                    if (error) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not publish post");
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
        };

        $scope.deletePost = function (post) {
            BlogService.deleteBlog(post.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Successfully deleted post", success.data.title);
                $scope.updatePosts();
            }, function () {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not delete post");
            })
        }
    }]);
