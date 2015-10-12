angular.module('app.modDoc', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
				.state('modDoc', {
					url: '/modDoc',
					templateUrl: 'app/pages/modDoc/modDocView.html',
					controller: "modDocController"
                });
        }])
	.controller("modDocController", ["$scope", function ($scope) {

    }]);