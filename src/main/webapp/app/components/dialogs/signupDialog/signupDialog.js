/**
 * Created by Terence on 3/26/2015.
 */
angular.module("app.signup", [])
    .controller("SignupDialogController", ["$scope", "AuthService", "vcRecaptchaService", "ToastService", "$location", "$mdDialog", function ($scope, AuthService, vcRecaptchaService, ToastService, $location, $mdDialog) {
        $scope.conflicts = [];
        $scope.credentials = {};

        $scope.$mdDialog = $mdDialog;

        $scope.setWidgetId = function (widgetId) {
            $scope.widgetId = widgetId;
        }

        $scope.setResponse = function (response) {
            console.log(response);

            $scope.credentials.captchaResponse = response;
        }

        $scope.signup = function (credentials) {
            $scope.conflicts = [];
            $scope.successMessages = [];

            var emailValid = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i.test(credentials.email);
            var usernameValid = /^([A-Za-z0-9\-_]+)$/.test(credentials.username);
            var passwordsMatch = credentials.password && credentials.password2 && credentials.password === credentials.password2;

            if (!usernameValid) {
                $scope.conflicts.push("Username can contain characters, numbers and underscores");
            }

            if (!emailValid) {
                $scope.conflicts.push("Invalid Email Address");
            }

            if (!passwordsMatch) {
                $scope.conflicts.push("Passwords do not match");
            }

            var routeParams = $location.search();

            if (routeParams.referral) {
                credentials.referral = routeParams.referral;
            }

            if (emailValid && passwordsMatch && usernameValid) {
                AuthService.signup(credentials, function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Sign Up Success", "We've sent you an email, it should arrive shortly", 5000);

                    $mdDialog.cancel();
                }, function (error) {
                    console.log(error);

                    if (error.status === 409) {
                        $scope.conflicts = error.data;
                    }
                });
            }
        }

    }]);
