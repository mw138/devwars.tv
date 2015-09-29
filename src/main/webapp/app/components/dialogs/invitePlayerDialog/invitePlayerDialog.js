angular.module("app.invitePlayerDialog", [])
    .controller("InvitePlayerDialogController", ["$scope", "$mdDialog", "UserService", function ($scope, $mdDialog, UserService) {
        $scope.$mdDialog = $mdDialog;

        console.log("invitePlayer");

        $scope.userInput = '';
        $scope.selectedPlayer = '';
        $scope.results = [];

        $scope.searchUser = function () {
            console.log($scope.userInput);
            if ($scope.userInput.length > 1) {
                UserService.http.searchUsers($scope.userInput)
                    .then(function (success) {
                        console.log("userSearch:", success);
                        $scope.results = success.data;
                        $scope.selectedPlayer = success.data[0];

                    })
            }
        };


        $scope.playerFormat = function (player) {
            return player.username;
        };


        $scope.invitePlayer = function (player) {
            $mdDialog.hide(player);
        }

    }]);
