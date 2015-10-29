/**
 * Created by Terence on 3/22/2015.
 */
angular.module("app")
    .controller("LoginDialogController", function ($scope, AuthService, $mdDialog, DialogService, UserService, ToastService) {

        $scope.$mdDialog = $mdDialog;

        $scope.login = function (credentials) {
            $scope.incorrectCredentials = false;

            AuthService.login(credentials,
                function (success) {
                    console.log(success);
                },
                function (error) {
                    if(error.status === 401) {
                        $scope.incorrectCredentials = true;
                    }
                    console.log(error);
                }
            );
        }

        $scope.oauthLogin = function (provider) {
            console.log(provider);
            window.location = "/v1/oauth/" + provider;
        }

        $scope.forgotPassword = function ($event) {
            $mdDialog.cancel();

            DialogService.getInputWithMessage("Forgot Password", "Please enter your email", $event, function (response) {
                UserService.http.initResetPassword(response)
                    .then(function (success) {
                        ToastService.showDevwarsToast("fa-check-circle", "Success", "Check your email");
                    }, angular.noop);
            })
        }
    });