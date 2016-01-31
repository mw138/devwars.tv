/**
 * Created by Terence on 3/28/2015.
 */
angular.module("app.gameControlPanel", [])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('gameControlPanel', {
                    url: '/gpanel',
                    templateUrl: '/app/pages/gameControlPanel/gameControlPanelView.html',
                    controller: "GameControlPanelController"
                });

        }])
    .controller("GameControlPanelController", ["$scope", "GameService", "$location", "AuthService", "$rootScope", "$mdDialog", "ToastService", "$interval", "DialogService", function ($scope, GameService, $location, AuthService, $rootScope, $mdDialog, ToastService, $interval, DialogService) {
        var $routeParams = $location.search();

        $scope.game = null;
        $scope.pickedDate = new Date();
        $scope.pickedTime = new Date();

        $scope.updateGame = function () {
            if ($routeParams.game) {
                GameService.getGame($routeParams.game, function (success) {
                    $scope.game = (success.data);

                    $scope.pickedDate = new Date($scope.game.timestamp);
                    $scope.pickedTime = new Date($scope.game.timestamp);

                    $scope.updateSignedUpUsers();
                }, function (error) {
                    console.log(error);
                });
            } else {
                GameService.currentGame(function (success) {
                    $scope.game = success.data;

                    $scope.pickedDate = new Date($scope.game.timestamp);
                    $scope.pickedTime = new Date($scope.game.timestamp);

                    $scope.updateSignedUpUsers();
                }, function (error) {
                    console.log(error);

                    GameService.nearestGame(function (nearestSuccess) {
                        $scope.game = nearestSuccess.data;

                        $scope.pickedDate = new Date($scope.game.timestamp);
                        $scope.pickedTime = new Date($scope.game.timestamp);

                        $scope.updateSignedUpUsers();
                    }, function (nearestError) {
                        console.log(nearestError);
                    });
                });
            }
        };

        $scope.updateSignedUpUsers = function () {
            GameService.pendingPlayers($scope.game.id, function (success) {
                console.log(success.data);
                $scope.signedUpUsers = success.data;
            }, function (error) {

            })
        }

        $scope.saveGame = function () {

            $scope.getGameTimestamp();

            //Remove all completed objectives is game has been removed
            for (var teamKey in $scope.game.teams) {
                var team = $scope.game.teams[teamKey];

                for (var completedObjectiveKey in team.completedObjectives) {
                    var completedObjective = team.completedObjectives[completedObjectiveKey].objective;

                    var foundObjective = false;
                    for (var gameObjectiveKey in $scope.game.objectives) {
                        var gameObjective = $scope.game.objectives[gameObjectiveKey];

                        if (gameObjective.id && completedObjective.id && gameObjective.id === completedObjective.id) {
                            foundObjective = true;
                        }
                    }

                    if (!foundObjective) {
                        team.completedObjectives.splice(completedObjectiveKey, 1);
                    }
                }
            }

            GameService.editGame($scope.game.id, JSON.stringify($scope.game), function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Saved Game");
                $scope.updateGame();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not save game.")
            })
        }

        $scope.addPlayer = function (game, $event) {
            $mdDialog.show({
                    templateUrl: "/app/components/dialogs/addPlayerDialog/addPlayerDialogView.html",
                    controller: "AddPlayerDialogController",
                    targetEvent: $event,

                    locals: {
                        game: game
                    }
                })
                .then(function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Successfully added player", success.newPlayer.user.username);
                    $scope.updateGame();
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not add player.");
                });
        }

        $scope.removePlayer = function (game, player, $event) {
            GameService.removePlayer(game.id, player.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Successfully removed player", player.user.username);
                $scope.updateGame();
            }, function (error) {
                console.log(error);
            })

            $event.stopPropagation();
        };

        $scope.editPlayer = function (game, player, team, $event) {
            $mdDialog.show({
                    templateUrl: "/app/components/dialogs/editPlayerDialog/editPlayerDialogView.html",
                    controller: "EditPlayerDialogController",
                    targetEvent: $event,

                    locals: {
                        game: game,
                        player: player,
                        team: team
                    }
                })
                .then(function (success) {
                    $scope.updateGame();
                    ToastService.showDevwarsToast("fa-check-circle", "Successfully edited player", success.oldPlayer.user.username);
                }, function (error) {

                })
        };

        $scope.getGameTimestamp = function () {
            if ($scope.pickedDate) {
                $scope.pickedDate.setHours(0);
                $scope.pickedDate.setMinutes(0);
                $scope.pickedDate.setSeconds(0);
                $scope.pickedDate.setMilliseconds(0);

                $scope.game.timestamp = $scope.pickedDate.getTime();

                if ($scope.pickedTime) {
                    $scope.game.timestamp += ((($scope.pickedTime.getHours() * 60) + $scope.pickedTime.getMinutes()) * 60 * 1000);
                }
            }

            return $scope.game.timestamp;
        };

        $scope.activateGame = function () {
            GameService.activateGame($scope.game.id, function (success) {
                $scope.game = success.data;
            }, angular.noop);
        };

        $scope.deactivateGame = function () {
            GameService.deactivateGame($scope.game.id, function (success) {
                $scope.game = success.data;
            }, angular.noop);
        };

        $scope.addPointsToTeamInGame = function (team, game, $event) {
            DialogService.getInputWithMessage("Add points to " + team.name, "Points", $event, function (success) {
                GameService.addPointsToTeam(team.id, game.id, 0, success, function (res) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Added " + success + " points to " + team.name);
                }, angular.noop);
            });
        };

        $scope.addXPToTeamInGame = function (team, game, $event) {
            DialogService.getInputWithMessage("Add XP to " + team.name, "XP", $event, function (success) {
                GameService.addPointsToTeam(team.id, game.id, success, 0, function (res) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Added " + success + " XP to " + team.name);
                }, angular.noop);
            });
        }

        $scope.toggleTeamObjective = function (team, objective) {
            if (!team.completedObjectives) {
                console.log("Resetting");
                team.completedObjectives = [];
            }

            if ($scope.teamHasObjective(team, objective)) {
                var index = -1;

                for (var i = 0; i < team.completedObjectives.length; i++) {
                    if (objective.id === team.completedObjectives[i].objective.id) {
                        index = i;
                        i = team.completedObjectives.length;
                    }
                }

                if (index > -1) {
                    team.completedObjectives.splice(index, 1);
                    console.log("Able to remove");
                }
            } else {
                team.completedObjectives.push({
                    team_id: team.id,
                    objective: objective
                });

                console.log("Able to add");
            }

            /*console.log("Does the team have the objective now?");
             console.log($scope.teamHasObjective(team, objective));
             console.log(team.completedObjectives);*/
        };

        $scope.teamHasObjective = function (team, objective) {
            for (var objectiveKey in team.completedObjectives) {
                var teamObjective = team.completedObjectives[objectiveKey].objective;

                if (teamObjective.id === objective.id) {
                    return true;
                }
            }
            //console.log("Returning false for " +  team.name + " : " + objective.objectiveText);
            return false;
        }

        $scope.removeObjectiveFromGame = function (objective, game) {
            if (game.objectives) {
                game.objectives.splice(game.objectives.indexOf(objective), 1);
            }

            for (var teamKey in game.teams) {
                var team = game.teams[teamKey];

                for (var completedObjectiveKey in team.completedObjectives) {
                    var completedObjective = team.completedObjectives[completedObjectiveKey].objective;

                    if (completedObjective.id === objective.id) {
                        team.completedObjectives.splice(team.completedObjectives.indexOf(completedObjective, 1));
                    }
                }
            }
        }

        $scope.endGame = function (game) {
            GameService.endGame(game.id, game.teams.red.win ? game.teams.red.id : game.teams.blue.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Ended Game");
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not end game");
            })
        }

        $rootScope.$on("$locationChangeStart", function (event, newState, oldState) {
            var newStateParams = newState.split("?")[1];
            var oldStateParams = oldState.split("?")[1];

            var newRootURL = newState.split("?")[0];
            var oldRootURL = oldState.split("?")[0];

            if (newRootURL === oldRootURL && newStateParams && !oldStateParams) location.reload();

            if (newRootURL === oldRootURL && oldStateParams && !newStateParams) location.reload();

            if (newRootURL === oldRootURL && newStateParams && oldStateParams && newStateParams !== oldStateParams) location.reload()
        });

        $scope.updateGame();
    }]);
