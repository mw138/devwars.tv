angular.module('app.teamInviteDialog', [])
    .controller("TeamInviteDialogController", ["$scope", "$mdDialog", "invites", function ($scope, $mdDialog, invites) {

        $scope.$mdDialog = $mdDialog;
        $scope.invites = invites;

        $scope.selectedInvite = $scope.invites[0];

        $scope.updateSelectedInvite = function (invite) {
            $scope.selectedInvite = invite;
        };
    }]);