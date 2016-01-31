angular.module("app.addBlogPostDialog", [])
    .controller("AddBlogPostDialogController", ["$scope", "BlogService", "$mdDialog", "ToastService", function ($scope, BlogService, $mdDialog, ToastService) {

        $scope.done = function () {
            var post = $scope.blog;

            if ($scope.tags)
                post.tags = $scope.tags.split(" ");

            if (post.title && post.description && post.text && post.image_url) {
                BlogService.createBlog(JSON.stringify($scope.blog), $mdDialog.hide, $mdDialog.cancel);
            } else {
                if (!$scope.title && !$scope.description) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing input fields");
                } else {
                    if (!$scope.title) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing title field");
                    } else if (!$scope.description) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing post field");
                    }
                }
            }
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        }
    }])
    .controller("EditBlogPostDialogController", ["$scope", "post", "$mdDialog", "BlogService", "ToastService", function ($scope, post, $mdDialog, BlogService, ToastService) {
        $scope.blog = post;

        $scope.tags = post.tags.join(" ");

        $scope.done = function () {
            var updatedPost = $scope.blog;

            if ($scope.tags)
                updatedPost.tags = $scope.tags.split(" ");

            BlogService.updateBlog(post.id, JSON.stringify(updatedPost), function (success) {
                $mdDialog.hide();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Couldn't update post");
            })
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        }
    }]);
