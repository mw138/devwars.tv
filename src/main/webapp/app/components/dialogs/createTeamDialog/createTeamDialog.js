angular.module('app.createTeamDialog', [])
    .controller("CreateTeamDialogController", ["$scope", "$mdDialog", function ($scope, $mdDialog) {
        $scope.$mdDialog = $mdDialog;
        $scope.teamName = 'Team Name';
        $scope.teamTag = '';
        
        
        $scope.submitTeam = function (teamName, teamTag) {
            if (teamName.lenght <= 25 && teamTag <= 5) {
                $mdDialog.hide(teamName, teamTag);
            }
        };


    }]);