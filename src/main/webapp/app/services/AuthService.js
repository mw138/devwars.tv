/**
 * Created by Terence on 3/22/2015.
 */
angular.module("app.AuthService", [])
    .factory("AuthService", ["$http", function ($http) {
        var AuthService = {};

        AuthService.callbacks = [];

        AuthService.roles = [
            "PENDING",
            "USER",
            "BLOGGER",
            "ADMIN"
        ];

        AuthService.init = function () {
            $http({
                method: "GET",
                url: "/v1/user/"
            })
                .then(function (success) {
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
        };

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

        AuthService.isAtLeast = function (min) {
            if(!AuthService.user) return false;

            var minIndex = AuthService.roles.indexOf(min);
            var userIndex = AuthService.roles.indexOf(AuthService.user.role);

            if(minIndex < 0) return false;

            return userIndex >= minIndex;
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

        AuthService.isLoggedIn = function () {
            return $http({
                url: "/v1/user/",
                method: "GET"
            }).then(function (success) {
                AuthService.user = success.data;
            }, angular.noop)
        };

        return AuthService;
    }]);