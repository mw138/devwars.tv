/**
 * Created by Terence on 3/21/2015.
 */
angular.module("app.header", ['ngCookies'])
    .controller("HeaderController", ["$scope", "$mdDialog", "AuthService", "GameService", "$filter", "DialogService", "$state", "$cookies", "$cookieStore", function ($scope, $mdDialog, AuthService, GameService, $filter, DialogService, $state, $cookies, $cookieStore) {

        AuthService.init();
        $scope.AuthService = AuthService;
        $scope.$state = $state;

        $scope.DialogService = DialogService;
        $scope.$cookies = $cookies;

        GameService.currentGame(function (success) {
            $scope.currentGame = success.data;

            $scope.currentGameMessage = "We're live right now : " + $filter('date')($scope.currentGame.timestamp, "shortTime") + " on " + $filter('date')($scope.currentGame.timestamp, "longDate");
        }, function (error) {
            $scope.currentGame = {}

            GameService.nearestGame(function (success) {
                $scope.nearestGame = success.data;

                $scope.nearestGameMessage = "Next Game @ " + $filter('date')($scope.nearestGame.timestamp, "shortTime") + " on " + $filter('date')($scope.nearestGame.timestamp, "longDate");
            }, function (error) {
                $scope.currentGame = {}
            });
        });

        $scope.logout = function () {
            AuthService.logout();
        }
    }])