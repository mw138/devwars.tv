angular.module('app.createTeamDialog', [])
    .controller("CreateTeamDialogController", ["$scope", "$mdDialog", function ($scope, $mdDialog) {
        $scope.$mdDialog = $mdDialog;
        $scope.teamName = '';
        
        
        $scope.submitTeam = function (teamName) {
            console.log("teamName", teamName);
            $mdDialog.hide(teamName);
        };

        $scope.cancelCreateTeam = function () {
            $mdDialog.cancel();
        }
    }]);