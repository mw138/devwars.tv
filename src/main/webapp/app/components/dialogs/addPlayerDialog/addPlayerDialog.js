/**
 * Created by Terence on 4/8/2015.
 */
/**
 * Created by Terence on 4/3/2015.
 */
angular.module("app.addPlayerDialog", [])
    .controller("AddPlayerDialogController", ["$scope", "game", "$mdDialog", "GameService", "ToastService", function ($scope, game, $mdDialog, GameService, ToastService) {

        $scope.game = game;
        $scope.$mdDialog = $mdDialog;

        $scope.pendingPlayers = [];

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
            $scope.teamSearchText = $scope.selectedTeam.name;
        }

        $scope.done = function () {
            $scope.conflicts = [];

            //If the moderator didn't click a user but the query still matches an available user
            if (!$scope.selectedUser && $scope.searchText) {
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
            } else if ($scope.selectedUser) { //If the moderator did click a player, just make a new player blob to add to the game.
                $scope.selectedPlayer = {
                    team_id: $scope.selectedTeam.id,
                    language: $scope.selectedLanguage,
                    user: {
                        id: $scope.selectedUser.id
                    }
                }
            }

            if ($scope.selectedPlayer) { //If all went well, add them to the game on the back end
                GameService.addPlayer($scope.selectedTeam.id, $scope.game.id, $scope.selectedPlayer, function (success) {
                    $mdDialog.hide({
                        newPlayer: success.data
                    });
                }, function (error) {
                    if (error.status === 409) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Player is already in game.");
                    } else {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not add player.");
                    }
                });
            } else {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not add player.");
            }

        }

        $scope.cancel = function () {

        }
    }]);
