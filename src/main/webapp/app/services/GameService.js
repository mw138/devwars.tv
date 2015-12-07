angular.module('app.GameService', [])
   .factory('GameService', ['$http', function($http){
 var GameService = {};
GameService.http = {};

/*
*/
GameService.nearestTournament = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/game/tournament/nearest',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.nearestTournament = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/tournament/nearest',
params: {}
})};/*
*/
GameService.upcomingTournaments = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/game/tournament/upcoming',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.upcomingTournaments = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/tournament/upcoming',
params: {}
})};/*
Required Role : ADMIN
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

GameService.http.pullCloudNineSites = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/sitepull',
params: {}
})};/*
Required Role : ADMIN
Path Variable {id} : int
Query Parameter {team} : int
*/
GameService.signupTeamForGame = function(id, team, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/game/' + id + '/addteamgamesignup',
data: {team : team}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.signupTeamForGame = function(id, team, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/game/' + id + '/addteamgamesignup',
data: {team : team}
})};/*
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

GameService.http.pastGames = function(offset, count, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/pastgames',
params: {offset : offset,count : count}
})};/*
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

GameService.http.upcomingGames = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/upcoming',
params: {}
})};/*
Query Parameter {firstGame} : int
Query Parameter {count} : int
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

GameService.http.getGameList = function(firstGame, count, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/pastgamelist',
params: {firstGame : firstGame,count : count}
})};/*
Required Role : ADMIN
Query Parameter {name} : java.lang.String
Query Parameter {time} : long
Query Parameter {tournament} : java.lang.Integer
*/
GameService.createGame = function(name, time, tournament, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/game/create',
params: {name : name,time : time,tournament : tournament}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.createGame = function(name, time, tournament, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/create',
params: {name : name,time : time,tournament : tournament}
})};/*
Required Role : ADMIN
*/
GameService.createLegacyGame = function(game, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/game/createlegacy',
data: {game : game}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.createLegacyGame = function(game, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/game/createlegacy',
data: {game : game}
})};/*
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

GameService.http.allGames = function(offset, count, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/',
params: {offset : offset,count : count}
})};/*
Required Role : ADMIN
*/
GameService.editGame = function(id, game, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/game/' + id + '/update',
data: {game : game}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.editGame = function(id, game, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/game/' + id + '/update',
data: {game : game}
})};/*
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

GameService.http.activateGame = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/activate',
params: {}
})};/*
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

GameService.http.deactivateGame = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/deactivate',
params: {}
})};/*
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

GameService.http.latestGame = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/latestgame',
params: {}
})};/*
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

GameService.http.deleteGame = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/delete',
params: {}
})};/*
Required Role : BLOGGER
Path Variable {id} : int
*/
GameService.siteArchive = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/game/' + id + '/sitearchive',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.siteArchive = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/sitearchive',
params: {}
})};/*
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

GameService.http.pendingPlayers = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/pendingplayers',
params: {}
})};/*
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

GameService.http.nearestGame = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/nearestgame',
params: {}
})};/*
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

GameService.http.editPlayer = function(teamID, id, playerID, json, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/team/' + teamID + '/player/' + playerID + '/edit',
params: {json : json}
})};/*
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

GameService.http.addPointsToTeam = function(teamID, id, xp, points, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/team/' + teamID + '/addpoints',
params: {xp : xp,points : points}
})};/*
Required Role : USER
Path Variable {id} : int
*/
GameService.applyTeamForGame = function(id, users, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/game/' + id + '/applyteam',
data: {users : users}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.applyTeamForGame = function(id, users, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/game/' + id + '/applyteam',
data: {users : users}
})};/*
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

GameService.http.endGame = function(id, winner, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/endgame',
params: {winner : winner}
})};/*
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

GameService.http.signUpTwitchUser = function(id, username, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/game/' + id + '/signuptwitchuser',
data: {username : username}
})};/*
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

GameService.http.getGame = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '',
params: {}
})};/*
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

GameService.http.resetGameWinner = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/resetwinner',
params: {}
})};/*
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

GameService.http.signUpForGame = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/signup',
params: {}
})};/*
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

GameService.http.addVotes = function(teamID, id, code, func, design, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/team/' + teamID + '/addvotes',
params: {code : code,func : func,design : design}
})};/*
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

GameService.http.forfeitUser = function(player, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/game/forfeituser',
data: {player : player}
})};/*
Path Variable {id} : int
*/
GameService.teamInfoForGame = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/game/' + id + '/teaminfo',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

GameService.http.teamInfoForGame = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/teaminfo',
params: {}
})};/*
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

GameService.http.currentGame = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/currentgame',
params: {}
})};/*
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

GameService.http.resignFromGame = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/game/' + id + '/resign',
params: {}
})};return GameService;
}]);
