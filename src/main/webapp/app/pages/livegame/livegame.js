/**
 * Created by Terence on 5/6/2015.
 */
angular.module("app.liveGame", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('livegame', {
                    url: '/live',
                    templateUrl: '/app/pages/livegame/livegameView.html',
                    controller: "LiveGameController"
                });

        }])
    .controller("LiveGameController", ["$scope", "GameService", function ($scope, GameService) {

        $scope.live = false;

        GameService.currentGame(function (success) {
            $scope.live = true;
        }, function (error) {
            $scope.live = false; //Redundancy
        })

    }]);