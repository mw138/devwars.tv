/**
 * Created by Beau on 01 May 2015.
 */
angular.module("app.help", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('help', {
                    url: '/help',
                    templateUrl: '/app/pages/help/helpView.html',
                    controller: "HelpController"
                });

        }])
    .controller("HelpController", ["$scope", function ($scope) {

    }]);

app.controller('HelpController', function ($scope, $location, $anchorScroll) {
    $scope.scrollTo = function (id) {
        $location.hash(id);
        $anchorScroll();
    }
});
