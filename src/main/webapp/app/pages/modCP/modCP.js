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

                    auth: true,

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

                    auth: true,

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

                    auth: true,

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

                    auth: true,

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

                    auth: true,

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

    .controller("ModCPController", ["$scope", "GameService", "ToastService", "$filter", "$mdDialog", "$location", "$http", "PlayerService", "TournamentService", function($scope, GameService, ToastService, $filter, $mdDialog, $location, $http, PlayerService, TournamentService){

        $scope.pickedDate = new Date();
        $scope.pickedTime = new Date();

        $scope.availableGames = null;

        $scope.upcomingTournaments = [];
        $scope.selectedTournament = null;

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

                if($location.search().game) {
                    var game = parseInt($location.search().game);

                    $scope.availableGames.forEach(function (a) {
                        if(a.id === game && !$scope.hasInit) {
                            $scope.hasInit = true;
                            $scope.selectedGame = a;
                        }
                    });
                };
            }, angular.noop);
        };

        $scope.gameLabel = function (game) {
            return game.id + " : " + $filter('date')(game.timestamp, 'mediumDate') + " - " + game.name;
        };

        $scope.tournamentLabel = function (tournament) {
            return tournament.id + " : " + tournament.start
        };

        $scope.createGame = function () {

            $scope.dateWatch();

            var tournamentId = null;
            if($scope.selectedTournament) {
                tournamentId = $scope.selectedTournament.id;
            }

            GameService.createGame($scope.name, $scope.gameDate.getTime(), tournamentId, function (success) {
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
                    PlayerService.addPlayer(success.team.id, success.language, JSON.stringify(player), function (playerSuccess) {
                        success.team.players.push(playerSuccess.data);
                    }, function () {
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

        $scope.removePlayer = function (player, team) {
            PlayerService.removePlayer(player.id, function () {
                console.log(team);
                team.players.splice(team.players.indexOf(player), 1);
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

                game.active = true;
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle",  "Error", "Could not activate game");
            })
        };

        $scope.deactivateGame = function (game) {
            GameService.deactivateGame(game.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Deactivated Game");

                game.active = false;
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

        $scope.$watch('selectedTournament', function () {
            $scope.appliedTournamentTeams = $scope.selectedTournament.teamSignups;
        });

        $scope.uploadFiles = function(team, file) {
            var formData = new FormData();

            formData.append("zip", dataURItoBlob(file));

            $http.post("/v1/team/" + team.id + "/upload", formData, {
                withCredentials: true,
                headers: {'Content-Type': undefined},
                transformRequest: angular.identity
            })
                .then(function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Uploaded files for team");
                }, function () {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not upload files");
                });
        };

        var dataURItoBlob = function(dataURI) {
            var binary = atob(dataURI.split(',')[1]);
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
            var array = [];
            for(var i = 0; i < binary.length; i++) {
                array.push(binary.charCodeAt(i));
            }
            return new Blob([new Uint8Array(array)], {type: mimeString});
        };

        TournamentService.http.upcomingTournaments()
            .then(function (success) {
                $scope.upcomingTournaments = success.data;


                console.log('upcoming t: ',success.data);
            }, angular.noop);

        $scope.applyTeamToGame = function (team) {
            console.log("ApplyTeamToGame: ", $scope.selectedGame.id, team.id);

            $mdDialog.show({
                templateUrl: "app/components/dialogs/confirmDialog/confirmationDialogView.html",
                controller: "ConfirmDialogController",

                locals: {
                    title: "Apply Team to Game",
                    message: "This will add all players from '" + team.name +
                    "' and apply them to " + $scope.selectedGame.name + " game on " + $filter('date')($scope.selectedGame.timestamp, 'mediumDate'),
                    yes: "Yes",
                    no: "No"
                }
            })
                .then(function () {
                    GameService.http.signupTeamForGame($scope.selectedGame.id, team.id)
                        .then(function (success) {
                            ToastService.showDevwarsToast("fa-check-circle", "Success", "Added Players");
                            $scope.updateSelectedGame();
                        }, function (error) {
                            ToastService.showDevwarsToast("fa-exclamation-circle", "Error", error.data);
                        })
                }, angular.noop)
        };



        $scope.updateGames();
    }]);






