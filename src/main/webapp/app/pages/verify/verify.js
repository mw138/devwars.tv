/**
 * Created by Beau on 07 May 2015.
 */
angular.module("app.verify", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('verify', {
                    url: '/verify',
                    templateUrl: '/app/pages/verify/verifyView.html',
                    controller: "VerifyController"
                });

        }])
    .controller("VerifyController", ["$scope", function ($scope) {

    }]);

app.controller('VerifyController', ["$scope", "$location", "$anchorScroll", "$interval", function ($scope, $location, $anchorScroll, $interval) {
    var routeParams = $location.search();

    $scope.username = routeParams.username;

    $scope.seconds = 10;

    $interval(function () {
        $scope.seconds--;

        if ($scope.seconds === 0) {
            location.href = "/#/dashboard";
        }
    }, 1000, 10, true);

    $scope.scrollTo = function (id) {
        $location.hash(id);
        $anchorScroll();
    }
}]);
