/**
 * Created by Terence on 5/4/2015.
 */
angular.module("app.InputDialog", [])
    .controller("InputDialogController", ["$scope", "$mdDialog", "title", "message", function ($scope, $mdDialog, title, message) {
        $scope.$mdDialog = $mdDialog;

        $scope.title = title;
        $scope.message = message;
    }]);