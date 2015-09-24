angular.module("app.disbandTeamDialog", [])
    .controller("DisbandTeamDialogController", ["$scope", "$mdDialog", "team", function ($scope, $mdDialog, team) {
        $scope.$mdDialog = $mdDialog;
        $scope.team = team;
        console.log("team", team);

        $scope.disbandTeam = function (id, name) {
            $mdDialog.hide({
                id: id,
                name: name
            });
        };


        $scope.selectedMember = '';
        $scope.assignAndLeave = function (selectedMember) {
            console.log("selectedMember", selectedMember);

        }
    }]);
