angular.module("app.disbandTeamDialog", [])
    .controller("DisbandTeamDialogController", ["$scope", "$mdDialog", "team", function ($scope, $mdDialog, team) {
        $scope.$mdDialog = $mdDialog;
        $scope.team = team;
        console.log("team", team);

        $scope.disbandTeam = function (id) {
            $mdDialog.hide(id, $scope.team.name);
        };


        $scope.selectedMember = '';
        $scope.assignAndLeave = function (selectedMember) {
            console.log("selectedMember", selectedMember);

        }
    }]);
