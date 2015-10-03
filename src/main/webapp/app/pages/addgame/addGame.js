angular.module('app.addGame', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('addGame', {
                    url: '/addgame',
                    templateUrl: 'app/pages/addgame/addGameView.html',
                    controller: "addGameController"
                });
        }])
    .controller("addGameController", ["$scope", function ($scope) {

    }]);