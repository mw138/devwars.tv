angular.module('app.ngSignupTeamGame', [])
    .directive('ngSignupTeamGame', function (AuthService, UserService, GameService) {
        return {
            restrict: 'A',

            scope: {
                'game': '=ngSignupTeamGame'
            },

            controller: function ($scope, $element, $mdDialog, ToastService) {
                if($scope.game.teamGame) {
                    $element.bind('click', function (event) {
                        UserService.http.getMyTeam()
                            .then(function (success) {
                                $mdDialog.show({
                                        templateUrl: "app/components/dialogs/confirmTeamSignupDialog/confirmTeamSignupDialogView.html",
                                        controller: "ConfirmTeamSignupDialogController",

                                        locals: {
                                            game: $scope.game,
                                            team: success.data,
                                            event: event
                                        }
                                    })
                                    .then(function (users) {
                                        GameService.http.applyTeamForGame($scope.game.id, JSON.stringify(users))
                                            .then(function (success) {
                                                ToastService.showDevwarsToast("fa-check-circle", "Success", "Applied Team for game");
                                            }, function (error) {
                                                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                                            });
                                    }, angular.noop);
                            }, angular.noop);

                        event.stopPropagation();
                    })
                }
            }
        };
    });