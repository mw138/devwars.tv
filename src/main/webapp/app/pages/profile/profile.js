/**
 * Created by Beau on 16 May 2015.
 */
angular.module("app.profile", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
				.state('profile', {
					url: '/profile',
					templateUrl: '/app/pages/profile/profileView.html',
                    controller: "ProfileController"
                });

        }])
	.controller("ProfileController", ["$scope", "AuthService", "UserService", "$location", function ($scope, AuthService, UserService, $location) {

        $scope.AuthService = AuthService;

        var routeParams = $location.search();

        if(routeParams.username) {
            UserService.getPublicUser(routeParams.username, function (success) {
                $scope.user = success.data;
            }, function (error) {
                console.log(error);
            })
        }
    }]);