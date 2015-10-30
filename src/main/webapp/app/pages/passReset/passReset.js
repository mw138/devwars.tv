/**
 * Created by Beau on 07 May 2015.
 */
angular.module('app.passReset', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('passwordReset', {
                    url: '/passwordreset?user&key',
                    templateUrl: 'app/pages/passReset/passResetView.html',
                    controller: "passResetController"
                });
        }])
    .controller("passResetController", function ($scope, $stateParams, UserService, ToastService, $timeout) {

        $scope.resetPassword = function ($event) {
            UserService.http.resetPassword($scope.password, $scope.passwordConfirmation, $stateParams.user, $stateParams.key)
                .then(function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Change password: Redirecting");

                    $timeout(function () {
                        location.href = "/";
                    }, 2000);
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                })
        }

    });