/**
 * Created by Beau on 05 October 2015.
 */
angular.module("app.rules", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('rules', {
                    url: '/rules',
                    templateUrl: '/app/pages/rules/rulesView.html',
                    controller: "RulesController"
                });

        }])
    .controller("RulesController", ["$scope", function ($scope) {

        $scope.underscore = "http://underscorejs.org/underscore-min.js";
        $scope.lodash = "https://raw.githubusercontent.com/lodash/lodash/3.10.1/lodash.js";
        $scope.jquery = "https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js";

        $scope.scrollTo = function(id) {
            $location.hash(id);
            $anchorScroll();
        }
    }]);
