angular.module('app.confirmTeamSignupDialog', [])
    .controller("ConfirmTeamSignupDialogController", ["$scope", "$mdDialog", function ($scope, $mdDialog) {
        $scope.$mdDialog = $mdDialog;
    }]);