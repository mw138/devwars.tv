angular.module('app.TournamentService', [])
   .factory('TournamentService', ['$http', function($http){
 var TournamentService = {};
TournamentService.http = {};

/*
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
Required Role : ADMIN
Query Parameter {game} : int
*/
TournamentService.signupTeamForTournamentFromGame = function(game, users, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/tournament/signupteam',
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
url: '/v1/tournament/signupteam',
data: {game : game,users : users}
})};/*
Required Role : USER
Path Variable {id} : int
*/
TournamentService.signupTeamForTournament = function(id, users, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/tournament/' + id + '/signupteam',
data: {users : users}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

TournamentService.http.signupTeamForTournament = function(id, users, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/tournament/' + id + '/signupteam',
data: {users : users}
})};return TournamentService;
}]);
