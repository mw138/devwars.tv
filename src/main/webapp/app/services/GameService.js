angular.module('app.GameService', [])
    .factory('GameService', ['$http', function($http){
        var GameService = {};

        /*
         Required Role : USER
         Path Variable {id} : int
         */
        GameService.resignFromGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/resign',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         */
        GameService.pendingPlayers = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/pendingplayers',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         */
        GameService.forfeitUser = function(player, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/game/forfeituser',
                data: {player : player}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         */
        GameService.deleteGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/delete',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        GameService.currentGame = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/currentgame',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        GameService.nearestGame = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/nearestgame',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {teamID} : int
         Path Variable {id} : int
         Path Variable {playerID} : int
         Query Parameter {json} : java.lang.String
         */
        GameService.editPlayer = function(teamID, id, playerID, json, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/team/' + teamID + '/player/' + playerID + '/edit',
                params: {json : json}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Query Parameter {offset} : int
         Query Parameter {count} : int
         */
        GameService.pastGames = function(offset, count, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/pastgames',
                params: {offset : offset,count : count}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        GameService.latestGame = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/latestgame',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        GameService.upcomingGames = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/upcoming',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         Query Parameter {username} : java.lang.String
         */
        GameService.signUpTwitchUser = function(id, username, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/game/' + id + '/signuptwitchuser',
                data: {username : username}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         */
        GameService.deactivateGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/deactivate',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         Query Parameter {json} : java.lang.String
         */
        GameService.editGame = function(id, json, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/game/' + id + '/update',
                data: {json : json}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Query Parameter {name} : java.lang.String
         Query Parameter {time} : long
         */
        GameService.createGame = function(name, time, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/create',
                params: {name : name,time : time}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : USER
         Path Variable {id} : int
         */
        GameService.signUpForGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/signup',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         */
        GameService.activateGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/activate',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Path Variable {id} : int
         */
        GameService.getGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         Query Parameter {winner} : int
         */
        GameService.endGame = function(id, winner, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/endgame',
                params: {winner : winner}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {teamID} : int
         Path Variable {id} : int
         Query Parameter {code} : int
         Query Parameter {func} : int
         Query Parameter {design} : int
         */
        GameService.addVotes = function(teamID, id, code, func, design, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/team/' + teamID + '/addvotes',
                params: {code : code,func : func,design : design}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Query Parameter {offset} : int
         Query Parameter {count} : int
         */
        GameService.allGames = function(offset, count, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/',
                params: {offset : offset,count : count}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         */
        GameService.resetGameWinner = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/resetwinner',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : ADMIN
         Path Variable {teamID} : int
         Path Variable {id} : int
         Query Parameter {xp} : int
         Query Parameter {points} : int
         */
        GameService.addPointsToTeam = function(teamID, id, xp, points, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/team/' + teamID + '/addpoints',
                params: {xp : xp,points : points}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Query Parameter {firstGame} : int
         Query Parameter {count} : org.hibernate.internal.SessionImpl
         */
        GameService.getGameList = function(firstGame, count, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/pastgamelist',
                params: {firstGame : firstGame,count : count}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Path Variable {id} : int
         */
        GameService.pullCloudNineSites = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/sitepull',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        return GameService;
    }]);
