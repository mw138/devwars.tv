angular.module("app.games", [])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('games', {
                    url: '/games',
                    templateUrl: '/app/pages/games/gamesView.html',
                    controller: "GameController"
                });
        }])
    .controller("GameController", function ($scope, GameService, AuthService, $mdDialog, $mdToast, $filter, ToastService, DialogService, $location, UserService, TournamentService) {
        $scope.games = [];
        $scope.AuthService = AuthService;

        $scope.selectedSeason = null;

        $scope.options = {
            thickness: 200,
            legend: false
        };

        $scope.labels = [
            'blue',
            'red'
        ];

        $scope.DialogService = DialogService;

        GameService.pastGames(0, 8, function (success) {
            var seasons = success.data;
            var games = [];

            for(var seasonKey in seasons) {
                var season = seasons[seasonKey];

                season.forEach(function (game) {
                    games.push(game);
                });
            }

            games.forEach(function (game) {
                _.values(game.teams).forEach(function (team) {
                    var newPlayers = [];

                    team.players.filter(function (player) {return player.language.toLowerCase() === "html"}).forEach(function (player) {
                        newPlayers.push(player);
                    });

                    team.players.filter(function(player) { return player.language.toLowerCase() === "css"}).forEach(function (player) {
                        newPlayers.push(player);
                    });

                    team.players.filter(function(player) { return player.language.toLowerCase() === "js"}).forEach(function (player) {
                        newPlayers.push(player);
                    });

                    team.players = newPlayers;
                })
            })

            $scope.pastGames = games;

            var game = $location.search().game;
            var season = $location.search().season;

            if(game && season)
            {
                $scope.pastGames = $scope.pastGames.filter(function (a) {
                    return a.season != season;
                });

                GameService.getGameList(game, 10, function (success) {
                    success.data.forEach(function (game) {
                        $scope.pastGames.push(game);
                    });

                    $scope.setSeasonSelected(season);
                }, angular.noop);
            } else $scope.setSeasonSelected(2);

        }, angular.noop);

        GameService.upcomingGames(function (success) {
            $scope.upcomingGames = success.data;
        }, angular.noop);

        $scope.isSeasonSelected = function (season) {
            return $scope.selectedSeason === season;
        };

        $scope.setSeasonSelected = function (season) {
            if($(document).width() > 840) {
                $scope.selectedGame = $scope.pastGames.filter(function (game) {
                    return game.season == season;
                })[0];
            } else {
                $scope.selectedGame = null;
            }

            $scope.selectedSeason = parseInt(season);
        };

        $scope.shouldGameShow = function (game) {
            return game.season == $scope.selectedSeason;
        };

        $scope.getBluePercentageFor = function (game, type) {
            var redVotes = game.teams.red[type + 'Votes'];
            var blueVotes = game.teams.blue[type + 'Votes'];

            var totalVotes = redVotes + blueVotes;

            return blueVotes / totalVotes * 100;
        };

        $scope.getVotePointsForTeam = function (game, team, type) {
            var redVotes = game.teams.red[type + 'Votes'];
            var blueVotes = game.teams.blue[type + 'Votes'];

            var totalVotes = redVotes + blueVotes;

            var teamVotes = team === "blue" ? blueVotes : redVotes;
            var otherVotes = team === "blue" ? redVotes : blueVotes;

            var teamPercentage = teamVotes / totalVotes;

            if (teamPercentage >= .66) return 2;

            if(teamPercentage >= .33) return 1;

            return 0;
        };

        $scope.signupForGame = function (game, $event) {
            if(game.hasTournament) {
                $scope.applyMyTeamForGame(game, $event);
                return;
            };

            if (AuthService.user && AuthService.user.role !== "PEDINNG") {
                DialogService.applyForGame(game, $event);
            } else {
                if(!AuthService.user) {
                    DialogService.signup($event);
                } else {
                    ToastService.showDevwarsErrorToast("fa-envelope-o", "Error", "Please confirm your email before applying for games.")
                }
            }
        };

        $scope.applyMyTeamForGame = function (game, $event) {
            if($scope.myTeam.owner.id !== AuthService.user.id) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "You must own the team to do that");
                return;
            }

            $mdDialog.show({
                    templateUrl: "app/components/dialogs/confirmTeamSignupDialog/confirmTeamSignupDialogView.html",
                    controller: "ConfirmTeamSignupDialogController",
                    event: $event,

                    locals: {
                        game: game,
                        team: $scope.myTeam
                    }
                })
                .then(function (users) {
                    TournamentService.http.signupTeamForTournamentFromGame(game.id, JSON.stringify(users))
                        .then(function (success) {
                            ToastService.showDevwarsToast("fa-check-circle", "Success", "Applied " + $scope.myTeam.name + " for game");
                        }, function (error) {
                            ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                        });
                }, angular.noop);
        };

        $scope.resignFromGame = function (game, $event) {
            DialogService.getConfirmationDialog("Confirmation", "Are you sure you would like to resign?", "Yes", "No", $event)
                .then(function () {
                    GameService.resignFromGame(game.id, function (success) {
                        ToastService.showDevwarsToast("fa-check-circle", "Success", "Resigned from game");
                        AuthService.init();
                    }, angular.noop);
                },  angular.noop);
        };

        $scope.setSelectedGame = function (game, $index) {
            $scope.selectedGame = game;


            if($index > -1 && (new Date().getTime() - $scope.lastTimeClicked) > 200) {
                if($(".game" + $index).next().hasClass("slide")) {
                    $(".game" + $index).next().slideToggle();
                    $(".game" + $index).next().removeClass("slide");
                } else {
                    $(".slide").slideToggle();
                    $(".slide").removeClass("slide");

                    $(".game" + $index).next().slideToggle();
                    $(".game" + $index).next().addClass("slide");
                }

                $scope.lastTimeClicked = new Date().getTime();
            }

            $scope.designData = $scope.dataForGameCategory($scope.selectedGame, 'design');
            $scope.funcData = $scope.dataForGameCategory($scope.selectedGame, 'func');
            $scope.codeData = $scope.dataForGameCategory($scope.selectedGame, 'code');
        };

        $scope.isGameSelected = function (game) {
            return game.id === $scope.selectedGame.id;
        };

        $scope.moderateGame = function (game) {
            window.location = "/#/gpanel?game=" + game.id;
        };

        $scope.dataForGameCategory = function (game, category) {
            var data = [];

            for(var teamKey in game.teams)
            {
                var team = game.teams[teamKey];

                if(team[category + "Votes"]) {
                    data.push({
                        label: team.name,
                        value: team[category + "Votes"],
                        color: team.name === "blue" ? "#03A9F4" : "#E91E63"
                    });
                }
            }

            return data;
        };

        $scope.getVotePointsEarned = function (teamName, game) {
            var votingPoints = $scope.getVotePointsForTeam(game, teamName, 'design');
            votingPoints += $scope.getVotePointsForTeam(game, teamName, 'func');

            var objectivePoints = game.teams[teamName].completedObjectives.length;

            if(objectivePoints == game.objectives.length && game.objectives.length > 0) {
                objectivePoints += 1;
            }

            return objectivePoints + votingPoints;
        };

        $scope.getTotalVotesForVote = function (game, vote) {
            var sum = 0;

            _.values(game.teams).forEach(function (team) {
                sum += team[vote + 'Votes'];
            });

            return sum;
        };

        $scope.getOtherTeam = function (team, game) {
            return game.teams[team.name === "red" ? "blue" : "red"];
        };

        $scope.teamHasObjective = function (team, objective) {
            for(var objectiveKey in team.completedObjectives) {
                var teamObjective = team.completedObjectives[objectiveKey].objective;

                if(teamObjective.id === objective.id) {
                    return true;
                }
            }
            return false;
        };

        $scope.loadMore = function () {
            var offset = $scope.pastGames.filter(function (game) {
                return game.season == $scope.selectedSeason;
            }).length;

            GameService.pastGames(offset, null, function (success) {
                success.data[$scope.selectedSeason].forEach(function (a) {
                    $scope.pastGames.push(a);
                })
            }, angular.noop);
        };

        GameService.nearestGame(function (success) {
            $scope.games.push(success.data);
        }, angular.noop);

        UserService.http.getMyTeam()
            .then(function (success) {
                $scope.myTeam = success.data;
            }, angular.noop);

        $scope.lastTimeClicked = new Date().getTime();
    });