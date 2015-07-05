/**
 * Created by Terence on 3/21/2015.
 */
angular.module("app.footer", [])
    .controller("footerController", ["$scope", "$mdDialog", "DialogService", function ($scope, $mdDialog, DialogService) {
        $scope.DialogService = DialogService;
    }])


