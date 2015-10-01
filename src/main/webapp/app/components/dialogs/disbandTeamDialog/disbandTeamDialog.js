angular.module("app.disbandTeamDialog", [])
    .controller("DisbandTeamDialogController", ["$scope", "$mdDialog", "team", function ($scope, $mdDialog, team) {
        $scope.$mdDialog = $mdDialog;
        $scope.team = team;
        $scope.selectedMember = '';

        $scope.disbandTeam = function (team) {
            $mdDialog.hide({
                action: 'disband',
                id: team.id,
                name: team.name
            });
        };


        $scope.assignAndLeave = function (selectedMember) {
            //if (selectedMember)
            //    $mdDialog.hide({
            //        action: 'assignAndLeave',
            //        id: team.id,
            //        name: selectedMember
            //    });
        }
    }]);
