/**
 * Created by Terence on 3/22/2015.
 */
angular.module("app.auth", [])
    .factory("AuthService", ["$http", function ($http) {
        var AuthService = {};

        AuthService.callbacks = [];

        AuthService.init = function () {
            $http({
                method: "GET",
                url: "/v1/user/"
            })
                .then(function (success) {
                    console.log(success);
                    AuthService.setUser(success.data);

                    for (var i = 0; i < AuthService.callbacks.length; i++) {
                        AuthService.callbacks[i](success.data);
                    }
                }, function (error) {

                });
        };

        AuthService.login = function (credentials, successCallback, errorCallback) {
            return $http({
                method: "GET",
                url: "/v1/user/login",
                params: credentials
            })
                .then(function (success) {
                    location.href = "/dashboard";
                }, function (error) {
                    console.log(error);
                    errorCallback(error);
                });
        };

        AuthService.signup = function (credentials, successCallback, errorCallback) {
            return $http({
                method: "POST",
                url: "/v1/user/create",
                params: credentials
            })
                .then(function (success) {
                    successCallback(success);
                }, function (error) {
                    errorCallback(error);
                })
        }

        AuthService.logout = function () {
            return $http({
                method: "GET",
                url: "/v1/user/logout"
            })
                .then(function (success) {
                    location.href = "/";
                }, function (error) {
                    console.log(error);
                })
        }

        AuthService.setUser = function (user) {
            AuthService.user = user;
        };

        AuthService.isAdmin = function () {
            return AuthService.user ? AuthService.user.role === "ADMIN" : false;
        };

        AuthService.hasProvider = function (provider) {
            if(!AuthService.user || !AuthService.user.connectedAccounts) return false;

            for(var key in AuthService.user.connectedAccounts) {
                var account = AuthService.user.connectedAccounts[key];

                if(account.provider.toUpperCase() === provider.toUpperCase() && account.disconnected == false) return true;
            }

            return false;
        };

        AuthService.hasConnectedToProvider = function (provider) {
            if(!AuthService.user || !AuthService.user.connectedAccounts) return false;

            for(var key in AuthService.user.connectedAccounts) {
                var account = AuthService.user.connectedAccounts[key];

                if(account.provider.toUpperCase() === provider.toUpperCase()) return true;
            }

            return false;
        };

        return AuthService;
    }]);