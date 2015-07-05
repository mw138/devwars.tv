/**
 * Created by Terence on 3/23/2015.
 */
var home = angular.module('app.home', [
    'ui.router'
]);

home.config(['$stateProvider',
    function ($stateProvider) {

        $stateProvider
            .state('home', {
                url: '/',
                templateUrl: '/app/pages/home/homeView.html',
                controller: "HomeController"
            });

    }]);

home.controller("HomeController", ["$scope", "InfoService", "DialogService", "BlogService", "$location", "$anchorScroll", "AuthService", function ($scope, InfoService, DialogService, BlogService, $location, $anchorScroll, AuthService) {

    $scope.DialogService = DialogService;
    $scope.AuthService = AuthService;

    $scope.updateInfo = function () {
        InfoService.allInfo(function (success) {
            $scope.info = success.data;
        }, function (error) {
            console.log(error);
        })

        InfoService.bitsLeaderboard(function (success) {
            $scope.bitsLeaderboard = success.data;
        }, function (error) {
            console.log(error);
        })

        InfoService.xpLeaderboard(function (success) {
            $scope.xpLeaderboard = success.data;
        }, function (error) {
            console.log(error);
        })

        BlogService.allPosts(function (success) {
            success.data.splice(2, success.data.length - 2);
            $scope.posts = success.data;

            console.log($scope.posts);
        }, function (error) {
            console.log(error);
        })
    };

    $scope.scrollToBlogPost = function (id) {
        location.href = "/blog#" + id;
    };

    $scope.updateInfo();
}]);