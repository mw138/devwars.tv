angular.module('app.teamInviteDialog', [])
    .controller("TeamInviteDialogController", ["$scope", "$mdDialog", "invites", function ($scope, $mdDialog, invites) {

        $scope.$mdDialog = $mdDialog;
        $scope.teams = invites;

        $scope.selectedTeam = $scope.teams[0];

        $scope.updateSelectedTeam = function (team) {
            $scope.selectedTeam = team;
        };
    }]);