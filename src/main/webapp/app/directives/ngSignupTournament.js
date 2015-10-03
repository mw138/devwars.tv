angular.module('app.ngSignupTournament', [])
    .directive('ngSignupTournament', function () {
        return {
            restrict: 'A',

            scope: {
                game: '=ngSignupTournament'
            },

            controller: function ($scope, $element, $mdDialog, GameService, ToastService, UserService) {
                UserService.http.getMyTeam()
                    .then(function (success) {
                        $scope.myTeam = success.data;
                    }, angular.noop);

                $element.bind('click', function ($event) {
                    $mdDialog.show({
                            templateUrl: "app/components/dialogs/confirmTeamSignupDialog/confirmTeamSignupDialogView.html",
                            controller: "ConfirmTeamSignupDialogController",

                            locals: {
                                game: $scope.game,
                                team: $scope.myTeam,
                                event: $event
                            }
                        })
                        .then(function (users) {
                            GameService.http.signUpTeamForGame($scope.game.id, JSON.stringify(users))
                                .then(function (success) {
                                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Applied " + $scope.myTeam.name + " for game");
                                }, function (error) {
                                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                                });
                        }, angular.noop);
                });
            }
        }
    });