/**
 * Created by Terence on 3/22/2015.
 */
angular.module("app")
    .controller("LoginDialogController", ["$scope", "AuthService", "$mdDialog", function ($scope, AuthService, $mdDialog) {

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
    }]);