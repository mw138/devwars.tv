angular.module('app.modCP', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('modCP', {
                    url: '/modCP',
                    abstract: true,
                    name: "modCP",

                    templateUrl: 'app/pages/modCP/modCPView.html',
                    controller: "ModCPController"
                })

                .state('modCP.createGameView', {
                    url: '/creategames',
                    parent: "modCP",

                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPcreateGameView.html",
                            controller: "ModCPController"
                        }
                    }
                })

                .state('modCP.createTeamsView', {
                    url: '/createteams',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPcreateTeamsView.html",
                            controller: "ModCPController"
                        }
                    }
                })

                .state('modCP.createObjectiveView', {
                    url: '/createobjectives',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPcreateObjectivesView.html",
                            controller: "ModCPController"
                        }
                    }
                })

                .state('modCP.liveGameView', {
                    url: '/livegame',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPliveGameView.html",
                            controller: "ModCPController"
                        }
                    }
                })

                .state('modCP.PostGameView', {
                    url: '/postgame',
                    parent: "modCP",

                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPPostGameView.html",
                            controller: "ModCPController"
                        }
                    }
                })
            ;
        }])

    .controller("ModCPController", ["$scope", "GameService", "ToastService", "$filter", "$mdDialog", "$location", function($scope, GameService, ToastService, $filter, $mdDialog, $location){

        $scope.pickedDate = new Date();
        $scope.pickedTime = new Date();

        $scope.availableGames = null;

        $scope.dateWatch = function () {
            $scope.gameDate = new Date($scope.pickedDate.getTime());

            $scope.gameDate.setHours($scope.pickedTime.getHours());
            $scope.gameDate.setMinutes($scope.pickedTime.getMinutes());
            $scope.gameDate.setSeconds(0);
            $scope.gameDate.setMilliseconds(0);
        };

        $scope.updateGames = function () {
            GameService.allGames(0, 10, function (success) {
                $scope.availableGames = success.data;

                console.log($location.search());
                if($location.search().game) {
                    var game = parseInt($location.search().game);

                    $scope.availableGames.forEach(function (a) {
                        if(a.id === game) $scope.selectedGame = a;
                    });
                };
            }, angular.noop);
        };

        $scope.gameLabel = function (game) {
            return $filter('date')(game.timestamp, 'mediumDate') + " - " + game.name;
        }

        $scope.createGame = function () {

            $scope.dateWatch();

            GameService.createGame($scope.name, $scope.gameDate.getTime(), function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Created Game");
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not create game");
            })

        };

        $scope.addPlayer = function (player) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/addSelectedPlayerDialog/addSelectedPlayerDialogView.html",
                controller: "AddSelectedPlayerDialogController",

                locals : {
                    player: player,
                    game: $scope.selectedGame
                }
            })
                .then(function (success) {
                    GameService.addPlayer(success.team.id, $scope.selectedGame.id, success.language, JSON.stringify(player), function (success) {
                        $scope.updateSelectedGame();
                    }, function (error) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not add player");
                    });
                }, angular.noop);
        };

        $scope.updateSelectedGame = function () {
            GameService.getGame($scope.selectedGame.id, function (success) {
                $scope.selectedGame = success.data;
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not refresh game");
            })
        };

        $scope.removePlayer = function (player) {
            GameService.removePlayer($scope.selectedGame.id, player.id, function (success) {
                $scope.updateSelectedGame();
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Removed " + player.user.username);
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamtion-circle", "Error", "Could not remove player");
            });
        };

        $scope.forfeitPlayer = function (player) {
            GameService.forfeitUser(JSON.stringify(player), function (success) {

                $scope.updateSelectedGame();

                ToastService.showDevwarsToast("fa-check-circle", "Success", "Forfeited Player");
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not forfeit player");
            });
        };

        $scope.toggleObjective = function (objective, team) {

            var hasObjective = $scope.teamHasObjective(team, objective);

            console.log("Has Objective : " + hasObjective);
            
            if(hasObjective) {
                team.completedObjectives = team.completedObjectives.filter(function (a) {
                    return a.objective.id !== objective.id;
                })
            } else {
                team.completedObjectives.push({
                    objective : objective,
                    team_id: team.id
                });
            }

            console.log(team.completedObjectives);
        };

        $scope.teamHasObjective = function(team, objective) {
            var hasObjective = false;

            team.completedObjectives.forEach(function (a) {
                if(a.objective.id == objective.id) hasObjective = true;
            });

            return hasObjective;
        };

        $scope.activateGame = function (game) {
            GameService.activateGame(game.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Activated Game");

                $scope.updateSelectedGame();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle",  "Error", "Could not activate game");
            })
        };

        $scope.deactivateGame = function (game) {
            GameService.deactivateGame(game.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Deactivated Game");

                $scope.updateSelectedGame();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle",  "Error", "Could not deactivate game");
            })
        };

        $scope.saveGame = function (game) {

            //Remove objectives with no text (They're empty)
            if(game.objectives) {
                game.objectives = game.objectives.filter(function (objective) {
                    return objective.objectiveText.length > 0;
                });
            }

            for(var teamKey in game.teams) {
                var team = game.teams[teamKey];

                //Cascade to team's completed objectives
                if(team.completedObjectives) {
                    team.completedObjectives = team.completedObjectives.filter(function (completed) {
                        var found = false;

                        if(game.objectives) {
                            game.objectives.forEach(function (objective) {
                                if (objective.id == completed.objective.id) found = true;
                            });
                        }

                        return found;
                    });
                }
            }

            //Init their order ids since they're already in order
            game.objectives.forEach(function (a, index) {
                a.orderID = index;
            });

            GameService.editGame(game.id, JSON.stringify(game), function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Saved game");

                $scope.selectedGame = success.data;
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not save game");
            })
        };

        $scope.endGame = function (game, winner) {
              GameService.endGame(game.id, winner.id, function (success) {
                  $scope.updateSelectedGame();

                  ToastService.showDevwarsToast("fa-check-circle", "Success", winner.name + " won : Game Over");
              }, function (error) {
                  ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not end game");
              });
        };

        //Watchers
        $scope.$watch('selectedGame', function (oldVal, newVal) {

            GameService.pendingPlayers($scope.selectedGame.id, function (success) {
                $scope.selectedGame.appliedUsers = success.data;
            }, angular.noop);

        });

        $scope.updateGames();
    }]);






