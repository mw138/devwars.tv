angular.module('app.settings', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('settings', {
                    url: '/settings',
                    name: "settings",

                    templateUrl: 'app/pages/settings/settingsView.html',
                    controller: ['$scope', '$state',
                        function( $scope, $state) {
                            if($state.current.name == "settings") {
                                $state.go('settings.accountView');
                            }
                        }]
                })

                .state('settings.accountView', {
                    url: '/',

                    auth: true,

                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsAccountView.html",
                            controller: "SettingsController"
                        }
                    }
                })

                .state('settings.profileView', {
                    url: '/profile/',

                    parent: 'settings',
                    auth: true,

                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsProfileView.html",
                            controller: "SettingsController"
                        }
                    }
                })

                .state('settings.notificationsView', {
                    url: '/notifications/',

                    parent: "settings",
                    auth: true,

                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsNotificationsView.html",
                            controller: "SettingsController"
                        }
                    }
                })

                .state('settings.connectView', {
                    url: '/connections/',

                    auth: true,

                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsConnectView.html",
                            controller: "SettingsController"
                        }
                    }
                })

                .state('settings.warriorView', {
                    url: '/warrior/',

                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsWarriorView.html",
                            controller: "WarriorRegController"
                        }
                    }
                });

        }])
    .controller("SettingsController", ["$scope", "AuthService", "$http", "$mdDialog", "ToastService", "UserService", "$location", "DialogService", function ($scope, AuthService, $http, $mdDialog, ToastService, UserService, $location, DialogService) {
        $scope.AuthService = AuthService;

        var routeParams = $location.search();

        $scope.initProfile = function () {
            if(!AuthService.user) {
                $scope.profile = {};
                AuthService.callbacks.push($scope.initProfile);
            } else {
                $scope.profile = AuthService.user;
            }
        };

        $scope.checkVeteran = function () {
            if(!AuthService.user) {
                AuthService.callbacks.push($scope.checkVeteran);
            } else {
                if(routeParams.veteran) {
                    var twitchUsername = null;

                    for(var key in AuthService.user.connectedAccounts) {
                        if(AuthService.user.connectedAccounts[key].provider === 'TWITCH') twitchUsername = AuthService.user.connectedAccounts[key].username;
                    }

                    DialogService.showConfirmDialog("Account Transfer", "We have record that you have played in Season 1 with your Twitch account " + twitchUsername + ". We have that username reserved for you. Would you like to claim it or keep your existing username? If you keep your username, we will release the username " + twitchUsername + " to the public.", "Claim " + twitchUsername, "Keep my username", null)
                        .then(function (success) {
                            UserService.claimTwitch(function (success) {
                                location.href = "/settings/connections"
                            }, function (error) {
                                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                            });
                        }, function (error) {
                            UserService.releaseUsername(function (success) {
                                location.href = "/settings/connections"
                            }, function (error) {
                                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                            });
                        });
                }
            }
        };

        $scope.disconnect = function (provider) {
            if(AuthService.hasProvider(provider)) {
                $http({
                    method: "GET",
                    url: "/v1/connect/" + provider +  "/disconnect"
                })
                    .then(function (success) {
                        console.log(success);
                        location.reload();
                    }, function (error) {
                        console.log(error);
                    });
            } else
            {
                location.href = "/v1/connect/" + provider;
            }
        };

        $scope.editAvatarImage = function (image, $event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/editAvatarImageDialog/editAvatarImageView.html",
                controller: "EditAvatarImageDialogController",
                clickOutsideToClose: false,

                locals: {
                    image: image
                }
            })
                .then(function (success) {
                    location.reload();
                }, function (error) {

                })
        };

        $scope.passwordsMatch = function (passwordChange) {
            return passwordChange.newPassword1 === passwordChange.newPassword2
        };

        $scope.changePassword = function (passwordChange) {
            if(passwordChange.newPassword1
                && passwordChange.newPassword2
                && passwordChange.newPassword1 === passwordChange.newPassword2) {

                UserService.changePassword(passwordChange.newPassword1, passwordChange.currentPassword, function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Changed your password");
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                })
            }
        };

        $scope.changeEmail = function (changeEmail) {
            if(changeEmail.newEmail && changeEmail.currentPassword) {
                UserService.changeEmail(changeEmail.newEmail, changeEmail.currentPassword, function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Changed Email");
                    AuthService.init();
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                })
            }
        };

        $scope.updateInfo = function (profile) {
            UserService.updateInfo(profile.company, profile.location, profile.url, profile.username, function (success) {
                location.reload();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
            })
        };

        $scope.$watch("selectedAvatarImage", function (oldVal, newVal) {

            if(oldVal !== newVal)
                $scope.editAvatarImage($scope.selectedAvatarImage);
        });

        $scope.initProfile();
    }]);