angular.module("app.DialogService", [])
    .factory("DialogService", ["GameService", "ToastService", "$mdDialog", "AuthService", "$filter", "$state", function (GameService, ToastService, $mdDialog, AuthService, $filter, $state) {
        var DialogService = {};

        DialogService.applyForNearestGame = function ($event) {
            GameService.nearestGame(function (nearestGameSuccess) {
                DialogService.applyForGame(nearestGameSuccess.data, $event);
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa fa-exclamation-circle", "Error", "No upcoming games");
            })
        };

        DialogService.applyForCurrentGame = function ($event) {
            GameService.currentGame(function (nearestGameSuccess) {
                DialogService.applyForGame(nearestGameSuccess.data, $event);
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa fa-exclamation-circle", "Error", "No upcoming games");
            })
        };

        DialogService.applyForGame = function (game, $event, successCallback, errorCallback) {
            if(AuthService.user && AuthService.user.warrior) {
                $mdDialog.show({
                    controller: "GameSignupConfirmDialogController",
                    templateUrl: "app/components/dialogs/confirmGameSignupDialog/confirmGameSignupDialogView.html",
                    targetEvent: $event,

                    locals: {
                        game: game
                    }
                })
                    .then(function (success) {
                        GameService.signUpForGame(success.game.id, function (signupSuccess) {
                            AuthService.init();
                            ToastService.showDevwarsToast("fa-calendar", "Game applied for", $filter("date")(game.timestamp, 'medium'));
                        }, function (error) {
                            ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                        })
                    }, null);
            } else
            {
                if(!AuthService.user) {
                    DialogService.login($event);
                } else {
                    $state.go("warriorReg");
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Please signup to be a warrior");
                }
            }
        };

        DialogService.login = function ($event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/loginDialog/loginDialogView.html",
                controller: "LoginDialogController",
                targetEvent: $event,

                clickOutsideToClose: true,
                escapeToClose: true
            })
                .then(function (success) {

                }, function (error) {
                    if(error && error.register) {
                        DialogService.signup($event);
                    }
                });
        };

        DialogService.signup = function ($event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/signupDialog/signupDialogView.html",
                controller: "SignupDialogController",
                targetEvent: $event,

                clickOutsideToClose: true,
                escapeToClose: true
            })
                .then(function (success) {

                }, function (error) {
                    if(error && error.login) {
                        DialogService.login($event);
                    }
                });
        };

        DialogService.getInputWithMessage = function (title, message, $event, callback) {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/inputDialog/inputDialogView.html",
                controller: "InputDialogController",
                targetEvent: $event,

                locals: {
                    title: title,
                    message: message
                }

            })
                .then(function (success) {
                    callback(success);
                }, function (error) {

                })
        };

        DialogService.showConfirmDialog = function (title, message, yes, no, $event) {
            return $mdDialog.show({
                templateUrl: "app/components/dialogs/confirmDialog/confirmDialogView.html",
                controller: "ConfirmDialogController",
                targetEvent: $event,

                locals: {
                    title: title,
                    message: message,
                    yes: yes,
                    no: no
                }

            });

        };

        DialogService.getConfirmationDialog = function (title, message, yes, no, $event) {
            return $mdDialog.show({
                templateUrl: "app/components/dialogs/confirmDialog/confirmationDialogView.html",
                controller: "ConfirmDialogController",
                targetEvent: $event,

                locals: {
                    title: title,
                    message: message,
                    yes: yes,
                    no: no
                }

            });

        };

        DialogService.getBase64ForImage = function (image, $event) {
            return $mdDialog.show({
                templateUrl: "app/components/dialogs/editAvatarImageDialog/editAvatarImageView.html",
                controller: "ChangeAvatarImageDialogController",

                locals: {
                    image: image
                }
            });
        }

        return DialogService;
    }]);