/**
 * Created by Terence on 4/6/2015.
 */
angular.module("app.toastService", [])
    .factory("ToastService", ["$mdToast", "$filter", function ($mdToast, $filter) {
        var ToastService = {};

        ToastService.showDevwarsToast = function (icon, title, message, delay) {
            return $mdToast.show({
                templateUrl: "/app/components/toasts/devwarsToast/devwarsToastView.html",
                controller: "DevWarsToastController",

                locals: {
                    faIcon: icon,
                    title: title,
                    message: message
                },

                delay: delay ? delay : 3000
            });
        };

        ToastService.showDevwarsErrorToast = function (icon, title, message, delay) {
            return $mdToast.show({
                templateUrl: "/app/components/toasts/devwarsToast/devwarsErrorToastView.html",
                controller: "DevWarsToastController",

                locals: {
                    faIcon: icon,
                    title: title,
                    message: message
                },

                delay: delay ? delay : 3000
            });
        };

        ToastService.showError = function (error, delay) {
            ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error, delay);
        };

        ToastService.showErrorList = function (errors) {

            var message = errors.map(function (error) {
                return $filter('camel')(error.field) + " " + error.defaultMessage;
            }).join("\n");

            return $mdToast.show({
                templateUrl: "/app/components/toasts/devwarsToast/devwarsErrorToastView.html",
                controller: "DevWarsToastController",

                locals: {
                    faIcon: "fa-exclamation-circle",
                    title: "Error",
                    message: message
                }
            })
        };

        return ToastService;
    }]);