angular.module('app.createTeamDialog', [])
    .controller("CreateTeamDialogController", ["$scope", "$mdDialog", function ($scope, $mdDialog) {
        $scope.$mdDialog = $mdDialog;
    }]);