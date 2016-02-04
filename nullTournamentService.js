angular.module('null.TournamentService', [])
   .factory('TournamentService', ['$http', function($http){
 var TournamentService = {};
TournamentService.http = {};

/*
Path Variable {id} : int
*/
TournamentService.getTournament = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/tournament/' + id + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

TournamentService.http.getTournament = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/tournament/' + id + '',
params: {}
})};/*
*/
TournamentService.upcomingTournaments = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/tournament/upcoming',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

TournamentService.http.upcomingTournaments = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/tournament/upcoming',
params: {}
})};/*
Required Role : USER
Query Parameter {game} : int
*/
TournamentService.signupTeamForTournamentFromGame = function(game, users, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/tournament/applyteamforgame',
data: {game : game,users : users}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

TournamentService.http.signupTeamForTournamentFromGame = function(game, users, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/tournament/applyteamforgame',
data: {game : game,users : users}
})};/*
Query Parameter {game} : int
*/
TournamentService.getTournamentFromGame = function(game, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/tournament/bygame',
params: {game : game}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

TournamentService.http.getTournamentFromGame = function(game, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/tournament/bygame',
params: {game : game}
})};/*
Required Role : USER
Path Variable {id} : int
*/
TournamentService.applyTeamForTournament = function(id, users, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/tournament/' + id + '/applyteam',
data: {users : users}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

TournamentService.http.applyTeamForTournament = function(id, users, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/tournament/' + id + '/applyteam',
data: {users : users}
})};return TournamentService;
}]);
