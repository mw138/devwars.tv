angular.module("app.user", [])
    .controller("UserController", ["$scope", "UserService", "ToastService", function ($scope, UserService, ToastService) {
        $scope.user = null;

        $scope.updateUser = function () {
            UserService.getUser($scope.user.id, function (success) {
                $scope.user = success.data;
            }, function (error) {
                console.log(error);
            });
        }

        $scope.setUserId = function (id) {
            UserService.getUser(id, function (success) {
                $scope.user = success.data;
                console.log($scope.user);
            }, angular.noop);
        }

        $scope.setUser = function (user) {
            $scope.user = user;
            console.log($scope.user);
        }

        $scope.addPoints = function (points) {
            UserService.addPoints($scope.user.id, 0, points, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", (points > 0 ? "Added" : "Subtracted") + " points", (points > 0 ? "Added" : "Subtracted") + " " + points + " points");
                $scope.updateUser();
            }, angular.noop);
        }
    }]);