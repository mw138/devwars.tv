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
        
    }]);

app.controller('RulesController', function($scope, $location, $anchorScroll) {
   $scope.scrollTo = function(id) {
      $location.hash(id);
      $anchorScroll();
   }
});
