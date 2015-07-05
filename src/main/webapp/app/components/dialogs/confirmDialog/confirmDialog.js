angular.module("app.confirmDialog", [])
    .controller("ConfirmDialogController", ["$scope", "$mdDialog", "title", "message", "yes", "no", function ($scope, $mdDialog, title, message, yes, no) {
        $scope.$mdDialog = $mdDialog;

        $scope.title = title;
        $scope.message = message;
        $scope.yes = yes;
        $scope.no = no;
    }]);