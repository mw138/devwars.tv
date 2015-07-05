/**
 * Created by Terence on 4/6/2015.
 */
angular.module("app.toastService", [])
    .factory("ToastService", ["$mdToast", function ($mdToast) {
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
        }

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
        }

        return ToastService;
    }]);