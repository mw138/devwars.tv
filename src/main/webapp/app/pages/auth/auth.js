angular.module('app.auth', [
        'ui.router'
    ])

    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('auth', {
                    url: '/login',
                    templateUrl: 'app/pages/auth/loginView.html',
                    controller: 'LoginController'
                });
        }])


    .controller('LoginController', function ($scope, $rootScope, AuthService, $http) {
    });
