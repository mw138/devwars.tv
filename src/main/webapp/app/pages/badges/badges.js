angular.module("app.badges", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('badges', {
                    url: '/badges',
                    templateUrl: '/app/pages/badges/badgesView.html',
                    controller: "BadgesController"
                });

        }])
    .controller("BadgesController", ["$scope", "$http", "BadgeService", function ($scope, $http, BadgeService) {

        $scope.imageNameFromName = function (name) {
            return name.toLowerCase().replace(/\s+/g, "-");
        }

        BadgeService.getAll(function (success) {
            $scope.badges = success.data;
        }, angular.noop);

    }]);