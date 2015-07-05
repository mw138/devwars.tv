angular.module('app.about', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('about', {
                    url: '/about',
                    templateUrl: 'app/pages/about/aboutView.html',
                    controller: "AboutController"
                });
        }])
    .controller("AboutController", ["$scope", function ($scope) {

    }]);