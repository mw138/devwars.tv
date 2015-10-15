angular.module('app.addGame', [
        'ui.router'
    ])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('addGame', {
                    url: '/addgame',
                    templateUrl: 'app/pages/addgame/addGameView.html',
                    controller: "AddGameController"
                });
        }])
    .controller("AddGameController", ["$scope", "$http", function ($scope, $http) {

        $scope.pickedDate = new Date();
        $scope.pickedTime = new Date();
        $scope. game = {
            objectives: []
        };

        $scope.submit = function (legacy) {
            $scope.setGameTimeFromDateAndTime(legacy, $scope.pickedDate, $scope.pickedTime);

            $http({
                url: "/v1/game/createlegacy",
                method: "POST",
                data: {
                    game: JSON.stringify(legacy)
                }
            })
                .then(function (success) {
                    console.log(success.data);
                }, angular.noop);
        };

        $scope.setGameTimeFromDateAndTime = function(game, pickedDate, pickedTime) {
            game.timestamp = new Date($scope.pickedDate.getTime());

            game.timestamp.setHours($scope.pickedTime.getHours());
            game.timestamp.setMinutes($scope.pickedTime.getMinutes());
            game.timestamp.setSeconds(0);
            game.timestamp.setMilliseconds(0);
        }
    }]);