angular.module('app.teamInviteDialog', [])
    .controller("TeamInviteDialogController", ["$scope", "$mdDialog", function ($scope, $mdDialog) {

        $scope.$mdDialog = $mdDialog;

    }]);