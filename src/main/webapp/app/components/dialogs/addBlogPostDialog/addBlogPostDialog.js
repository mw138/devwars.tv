angular.module("app.addBlogPostDialog", [])
    .controller("AddBlogPostDialogController", ["$scope", "BlogService", "$mdDialog", "ToastService", function ($scope, BlogService, $mdDialog, ToastService) {
        $scope.done = function () {
            if($scope.title && $scope.description && $scope.text && $scope.image_url) {
                BlogService.createBlog($scope.image_url, $scope.description, $scope.text, $scope.title, $mdDialog.hide, $mdDialog.cancel);
            } else {
                if(!$scope.title && !$scope.description) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing input fields");
                } else {
                    if(!$scope.title) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing title field");
                    } else if(!$scope.description) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing post field");
                    }
                }
            }
        }

        $scope.cancel = function () {
            $mdDialog.cancel();
        }
    }])
    .controller("EditBlogPostDialogController", ["$scope", "post", "$mdDialog", "BlogService", "ToastService", function($scope, post, $mdDialog, BlogService, ToastService) {
        $scope.title = post.title;
        $scope.image_url = post.image_url;
        $scope.description = post.description;
        $scope.text = post.text;

        $scope.done = function () {
            BlogService.updateBlog(post.id, $scope.image_url, $scope.description, $scope.text, $scope.title, function (success) {
                console.log(success);
                $mdDialog.hide();
            }, function (error) {
                console.log(error);
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Couldn't update post");
            })
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        }
    }]);