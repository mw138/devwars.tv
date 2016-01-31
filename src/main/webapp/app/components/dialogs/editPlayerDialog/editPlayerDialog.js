/**
 * Created by Terence on 4/3/2015.
 */
angular.module("app.editPlayerDialog", [])
    .controller("EditPlayerDialogController", ["$scope", "game", "player", "team", "$mdDialog", "GameService", "ToastService", function ($scope, game, player, team, $mdDialog, GameService, ToastService) {

        $scope.game = game;
        $scope.player = player;

        $scope.selectedUser = player.user;
        $scope.selectedLanguage = player.language;
        $scope.selectedTeam = team;

        $scope.searchText = player.user.username;
        $scope.teamSearchText = team.name;

        $scope.pendingPlayers = [];

        $scope.$mdDialog = $mdDialog;

        GameService.pendingPlayers(game.id, function (success) {
            console.log(success.data);
            $scope.game.pendingPlayers = success.data;
        }, function (error) {
            console.log(error);
        })

        $scope.teamSearchQuery = function () {
            var teamsArray = [];

            for (var lang in game.teams) {
                teamsArray.push(game.teams[lang]);
            }

            return teamsArray.filter(function (team) {
                console.log($scope.selectedTeam);
                return team.name.toLowerCase().indexOf($scope.teamSearchText.toLowerCase()) > -1 || $scope.teamSearchText.toLowerCase().indexOf(team.name.toLowerCase()) > -1;
            });
        };

        $scope.pendingPlayersQuery = function () {
            if ($scope.game.pendingPlayers) {
                return $scope.game.pendingPlayers.filter(function (user) {
                    return user.username.toLowerCase().indexOf($scope.searchText.toLowerCase()) > -1 || $scope.searchText.toLowerCase().indexOf(user.username.toLowerCase()) > -1;
                });
            }
        }

        $scope.selectedUserChange = function () {
            $scope.searchText = $scope.selectedUser.username;
        }

        $scope.selectedTeamChange = function () {
            console.log("Team Change");
            $scope.teamSearchText = $scope.selectedTeam.name;
        }

        $scope.done = function () {
            $scope.conflicts = [];

            if ($scope.selectedUser === player.user && $scope.searchText) {
                for (var i = 0; i < $scope.game.pendingPlayers.length; i++) {
                    var pendingUser = $scope.game.pendingPlayers[i];

                    if (pendingUser.username.toUpperCase() === $scope.searchText.toUpperCase()) {
                        $scope.selectedPlayer = {
                            team_id: $scope.selectedTeam.id,
                            language: $scope.selectedLanguage,
                            user: {
                                id: pendingUser.id
                            }
                        };
                    }
                }
            } else if ($scope.selectedUser !== player.user) {
                $scope.selectedPlayer = {
                    team_id: $scope.selectedTeam.id,
                    language: $scope.selectedLanguage,
                    user: {
                        id: $scope.selectedUser.id
                    }
                }
            }

            if ($scope.selectedPlayer && $scope.selectedPlayer.user !== player.user) {
                GameService.editPlayer($scope.selectedTeam.id, $scope.game.id, player.id, $scope.selectedPlayer, function (success) {
                    $mdDialog.hide({
                        oldPlayer: player,
                        newPlayer: success.data
                    });
                }, angular.noop);
            } else {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not edit player.");
            }

        }

        $scope.cancel = function () {
            $mdDialog.cancel();
        }
    }]);
