/**
 * Created by Terence on 4/22/2015.
 */
angular.module("app.contact", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('contact', {
                    url: '/contact',
                    templateUrl: '/app/pages/contact/contactView.html',
                    controller: "ContactController"
                });

        }])
    .controller("ContactController", ["$scope", "ToastService", "ContactService", "AuthService", function ($scope, ToastService, ContactService, AuthService) {

        $scope.form = {};
        $scope.AuthService = AuthService;

        $scope.setDefaults = function () {
            if (AuthService.user) {
                $scope.form.name = AuthService.user.username;
                $scope.form.email = AuthService.user.email;
            } else {
                AuthService.callbacks.push($scope.setDefaults);
            }
        };

        $scope.submit = function (form) {
            if (form.name &&
                form.email &&
                form.type &&
                form.text) {

                if (form.text.length > 1000) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Enquiry too long");
                } else {
                    ContactService.create(form.name, form.text, form.type, form.email, function (success) {
                        ToastService.showDevwarsToast("fa-check-circle", "Success", "We have received your feedback");

                        delete $scope.form;
                    }, function (error) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Something went wrong");
                    })
                }
            } else {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing fields");
            }
        };

        $scope.setDefaults();

    }]);
