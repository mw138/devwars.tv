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
    .controller("GameController", ["$scope", "GameService", "AuthService", "$mdDialog", "$mdToast", "$filter", "ToastService", "DialogService", function ($scope, GameService, AuthService, $mdDialog, $mdToast, $filter, ToastService, DialogService) {
        $scope.games = [];
        $scope.AuthService = AuthService;

        $scope.options = {
            thickness: 200,
            legend: false
        };

        $scope.labels = [
            'blue',
            'red'
        ]

        $scope.DialogService = DialogService;

        GameService.pastGames(function (success) {
            $scope.pastGames = success.data;

            for(var key in $scope.pastGames) {
                var game = $scope.pastGames[key];

                for(var teamKey in game.teams) {
                    var team = game.teams[teamKey];

                    var players = team.players;
                    var sortedPlayers = [];

                    for(var playerKey in players) {
                        var player = players[playerKey];

                        if(player.language.toLowerCase() === "html") sortedPlayers[0] = player;
                        if(player.language.toLowerCase() === "css") sortedPlayers[1] = player;
                        if(player.language.toLowerCase() === "js") sortedPlayers[2] = player;
                    }

                    console.log(sortedPlayers);

                    team.players = sortedPlayers;
                }
            }

            if($(document).width() > 840)
                $scope.setSelectedGame($scope.pastGames[0]);
        }, angular.noop);

        GameService.upcomingGames(function (success) {
            $scope.upcomingGames = success.data;
        }, angular.noop);

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
        }

        $scope.isGameSelected = function (game) {
            return game.id === $scope.selectedGame.id;
        }

        $scope.moderateGame = function (game) {
            window.location = "/#/gpanel?game=" + game.id;
        }

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
            var otherTeam = null;
            var team = null;

            if(teamName === "red") {
                team = game.teams['red'];
                otherTeam = game.teams['blue'];
            } else if(teamName === "blue") {
                team = game.teams['blue'];
                otherTeam = game.teams['red'];
            }

            var total = 0;

            if(team.designVotes >= otherTeam.designVotes && (team.designVotes !== 0 && otherTeam.designVotes !== 0)) total+=2;
            if(team.funcVotes >= otherTeam.funcVotes  && (team.funcVotes !== 0 && otherTeam.funcVotes !== 0)) total+=2;
            if(team.codeVotes >= otherTeam.codeVotes  && (team.codeVotes !== 0 && otherTeam.codeVotes !== 0)) total+=2;

            //Last objective is two so add one if they aced
            if(team.completedObjectives.length === game.objectives.length) {
                total++;
            }

            return total;
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

        $scope.lastTimeClicked = new Date().getTime();
    }]);