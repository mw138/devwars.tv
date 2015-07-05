/**
 * Created by Terence on 4/6/2015.
 */
angular.module("app.devwarsToast", [])
    .controller("DevWarsToastController", ["$scope", "$mdDialog", "faIcon", "title", "message", function ($scope, $mdDialog, faIcon, title, message) {
        $scope.faIcon = faIcon;
        $scope.title = title;
        $scope.message = message;
    }])