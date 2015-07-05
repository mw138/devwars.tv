angular.module("app.sidebar", [])
    .directive("sidebar", function () {
        return {
            restrict: "E",

            controller: function ($scope, InfoService) {
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
                };

                $scope.updateInfo();
            },

            templateUrl: "/app/directives/sidebarDirective/sidebarView.html"
        };
    })