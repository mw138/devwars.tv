angular.module("app.invitePlayerDialog", [])
    .controller("InvitePlayerDialogController", ["$scope", "$mdDialog", "UserService", function ($scope, $mdDialog, UserService) {

        console.log("invitePlayer");

        $scope.userInput = '';

        $scope.searchUser = function () {
            console.log($scope.userInput);
            if ($scope.userInput.length > 1) {
                UserService.http.searchUsers($scope.userInput)
                    .then(function (success) {
                        console.log("userSearch:", success);
                    })
            }
        }

    }]);
