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
    .controller("GameController", ["$scope", "GameService", "AuthService", "$mdDialog", "$mdToast", "$filter", "ToastService", "DialogService", "$location", function ($scope, GameService, AuthService, $mdDialog, $mdToast, $filter, ToastService, DialogService, $location) {
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

                    team.players.filter(function(player) { return player.language.toLowerCase() === "js"}).forEach(function (player) {
                        newPlayers.push(player);
                    });

                    team.players.filter(function(player) { return player.language.toLowerCase() === "css"}).forEach(function (player) {
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
            } else $scope.setSeasonSelected(1);

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
            if (AuthService.user && AuthService.user.role !== "PENDING") {
                DialogService.applyForGame(game, $event);
            } else {
                if(!AuthService.user) {
                    DialogService.signup($event);
                } else {
                    ToastService.showDevwarsErrorToast("fa-envelope-o", "Error", "Please confirm your email before applying for games.")
                }
            }
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

            if(objectivePoints == game.objectives.length) {
                objectivePoints += 1;
            }

            return objectivePoints + votingPoints;
        };

        $scope.getOtherTeam = function (team, game) {
            return game.teams[team.name === "red" ? "blue" : "red"];
        };

        $scope.getAllGames = function (success) {
            GameService.allGames(0, 5, function (allGamesSuccess) {
                var returnGames = allGamesSuccess.data;

                $scope.games = $scope.games.concat(returnGames);

                for (var i = 0; i < $scope.games.length; i++) {
                    var game = $scope.games[i];

                    if (success && game.id === success.data.id) {
                        $scope.games.splice(i, 1);
                        i = $scope.games.length;
                    }
                }

                console.log($scope.games);
            }, function (allGamesError) {
                console.log(allGamesError);
            });
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

        $scope.sortPlayers = function (data) {
            var langs = ["html", "css", "js"];

            data.sort(function (a) {
                return langs.indexOf(a.language.toLowerCase());
            });
        };

        $scope.timezone = function () {
            var timeString = new Date().toString();

            var words = timeString.split('(')[timeString.split('(').length -1];
            words = words.split(')')[0];
            words = words.split(' ');

            var returnString = "";

            for(var wordKey in words) {
                var word = words[wordKey];

                returnString += word.charAt(0);
            }

            return returnString;
        };

        GameService.nearestGame(function (success) {
            $scope.games.push(success.data);

            $scope.getAllGames(success);
        }, function (error) {
            console.log(error);
            $scope.getAllGames();
        });

        angular.element('.gameListColumn__container').bind('scroll', function (event) {
            if( event.target.scrollTop >= (event.target.scrollHeight - event.target.offsetHeight)) {
                var offset = $scope.pastGames.filter(function (game) {
                    return game.season == $scope.selectedSeason;
                }).length;

                GameService.pastGames(offset, null, function (success) {
                    success.data[1].forEach(function (a) {
                        $scope.pastGames.push(a);
                    })
                }, angular.noop );
            }
        });

        $scope.lastTimeClicked = new Date().getTime();
    }]);