/**
 * Created by Beau on 07 May 2015.
 */
angular.module('app.passReset', [
    'ui.router'
])
    .config(['$stateProvider',
             function ($stateProvider) {

                 $stateProvider
                     .state('passReset', {
                     url: '/passReset',
                     templateUrl: 'app/pages/passReset/passResetView.html',
                     controller: "passResetController"
                 });
             }])
    .controller("passResetController", ["$scope", function ($scope) {

    }]);