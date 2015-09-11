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
                controller: "HomeController",

                resolve: {
                    allInfo: ['InfoService', function (InfoService) {
                        return InfoService.http.allInfo();
                    }],

                    bitsLeaderboard: ['InfoService', function (InfoService) {
                        return InfoService.http.bitsLeaderboard();
                    }],

                    xpLeaderboard: ['InfoService', function (InfoService) {
                        return InfoService.http.xpLeaderboard();
                    }],

                    blogPosts: ['BlogService', function (BlogService) {
                        return BlogService.http.allPosts();
                    }]
                }
            });

    }]);

home.controller("HomeController", function ($scope, InfoService, DialogService, BlogService, $location, $anchorScroll, AuthService, allInfo, bitsLeaderboard, xpLeaderboard, blogPosts) {

    $scope.info = allInfo.data;
    $scope.bitsLeaderboard = bitsLeaderboard.data;
    $scope.xpLeaderboard = xpLeaderboard.data;
    $scope.posts = blogPosts.data.splice(3, blogPosts.data.length - 3);

    $scope.DialogService = DialogService;
    $scope.AuthService = AuthService;

    $scope.updateInfo = function () {
        InfoService.allInfo(function (success) {
            $scope.info = success.data;
        }, function (error) {
            console.log(error);
        });

        InfoService.bitsLeaderboard(function (success) {
            $scope.bitsLeaderboard = success.data;
        }, function (error) {
            console.log(error);
        });

        InfoService.xpLeaderboard(function (success) {
            $scope.xpLeaderboard = success.data;
        }, function (error) {
            console.log(error);
        });

        BlogService.allPosts(function (success) {
            success.data.splice(3, success.data.length - 3);
            $scope.posts = success.data;
        }, function (error) {
            console.log(error);
        });
    };

    $scope.scrollToBlogPost = function (id) {
        location.href = "/blog#" + id;
    };
});