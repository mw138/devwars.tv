var home = angular.module('app.coming', [
    'ui.router'
]);

home.config(['$stateProvider',
    function ($stateProvider) {

        $stateProvider
            .state('coming', {
                url: '/coming',
                templateUrl: '/app/pages/coming/comingView.html'
            });

    }]);