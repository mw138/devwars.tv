angular.module("app.tournament", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('tournament', {
                    url: '/tournament',
                    templateUrl: '/app/pages/tournament/tournamentView.html',
                    controller: "TournamentController"
                });

        }])
    .controller("TournamentController", ["$scope", function ($scope) {

    }]);
