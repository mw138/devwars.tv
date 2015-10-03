angular.module('app.confirmTeamSignupDialog', [])
    .controller("ConfirmTeamSignupDialogController", ["$scope", "$mdDialog", "game", "team", function ($scope, $mdDialog, game, team) {
        $scope.$mdDialog = $mdDialog;

        $scope.game = game;
        $scope.team = team;

        $scope.selectedPlayers = [];

        $scope.isSelected = function (user) {
            return $scope.selectedPlayers.indexOf(user) > -1;
        };

        $scope.removePlayer = function (user) {
            $scope.selectedPlayers.splice($scope.selectedPlayers.indexOf(user), 1);
        };

        $scope.clickPlayer = function (user) {
            if($scope.isSelected(user)) {
                $scope.removePlayer(user);
            } else {
                console.log($scope.selectedPlayers.length);
                if($scope.selectedPlayers.length < 3)
                    $scope.selectedPlayers.push(user);
            }
        };

        $scope.confirmSignUp = function () {
            $mdDialog.hide($scope.selectedPlayers);
        }
    }]);