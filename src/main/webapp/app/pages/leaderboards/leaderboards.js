angular.module('app.leaderboards', [
	'ui.router'
])
	.config(['$stateProvider',
			 function ($stateProvider) {

				 $stateProvider
					 .state('leaderboards', {
					 url: '/leaderboards',
					 templateUrl: 'app/pages/leaderboards/leaderboardsView.html',
					 controller: "LeaderboardsController"
				 });
			 }])
	.controller("LeaderboardsController", ["$scope", "InfoService", function ($scope, InfoService) {

        $scope.page = 0;
        $scope.users = [];

        $scope.loadMore = function () {
            InfoService.leaderboard($scope.page, function (success) {
                for(var key in success.data) {
                    var user = success.data[key][0];
                    user.score = success.data[key][1];
                    $scope.users.push(user);
                }

                $scope.page++;
            }, function (error) {
                console.log(error);
            });
        };

        $scope.loadMore();

	}]);