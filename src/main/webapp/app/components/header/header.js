/**
 * Created by Terence on 3/21/2015.
 */
angular.module("app.header", ['ngCookies'])
    .controller("HeaderController", ["$scope", "$mdDialog", "AuthService", "GameService", "$filter", "DialogService", "$state", "$cookies", "$cookieStore", "UserService", "ToastService", function ($scope, $mdDialog, AuthService, GameService, $filter, DialogService, $state, $cookies, $cookieStore, UserService, ToastService) {

        AuthService.init();
        $scope.AuthService = AuthService;
        $scope.$state = $state;

        $scope.hasInit = false;

        $scope.DialogService = DialogService;
        $scope.$cookies = $cookies;

        GameService.currentGame(function (success) {
            $scope.currentGame = success.data;

            $scope.currentGameMessage = "We're live right now : " + $filter('date')($scope.currentGame.timestamp, "shortTime", "UTC") + " on " + $filter('date')($scope.currentGame.timestamp, "longDate", "UTC");
        }, function (error) {
            $scope.currentGame = {}

            GameService.nearestGame(function (success) {
                $scope.nearestGame = success.data;

                $scope.nearestGameMessage = "Next Game @ " + $filter('date')($scope.nearestGame.timestamp, "shortTime", "UTC") + " on " + $filter('date')($scope.nearestGame.timestamp, "longDate", "UTC");
            }, function (error) {
                $scope.currentGame = {}
            });
        });

        $scope.logout = function () {
            AuthService.logout();
        };

        UserService.getUnreadNotifications(function (success) {
            success.data.forEach(function (a, index) {
                setTimeout(function () {
                    ToastService.showDevwarsToast("fa-check-circle", "Notification", a.notificationText);
                }, index * 3000)
            });

            UserService.markNotificationsAsRead(JSON.stringify(success.data), angular.noop, angular.noop);
        }, angular.noop);
    }]);