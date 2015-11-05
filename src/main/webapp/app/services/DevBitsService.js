angular.module('app.DevBitsService', [])
   .factory('DevBitsService', ['$http', function($http){
 var DevBitsService = {};
DevBitsService.http = {};

/*
Path Variable {twitchUsername} : java.lang.String
*/
DevBitsService.getTwitchUser = function(twitchUsername, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/devbits/' + twitchUsername + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

DevBitsService.http.getTwitchUser = function(twitchUsername, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/devbits/' + twitchUsername + '',
params: {}
})};/*
Required Role : ADMIN
*/
DevBitsService.earnBetsForUsers = function(bets, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/devbits/earnedbets',
data: {bets : bets}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

DevBitsService.http.earnBetsForUsers = function(bets, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/devbits/earnedbets',
data: {bets : bets}
})};/*
Required Role : ADMIN
*/
DevBitsService.earnBetsForUsers = function(usernames, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/devbits/watched',
data: {usernames : usernames}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

DevBitsService.http.earnBetsForUsers = function(usernames, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/devbits/watched',
data: {usernames : usernames}
})};/*
Required Role : ADMIN
*/
DevBitsService.allIn = function(usernames, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/devbits/allin',
data: {usernames : usernames}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

DevBitsService.http.allIn = function(usernames, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/devbits/allin',
data: {usernames : usernames}
})};/*
Path Variable {amount} : int
Path Variable {twitchUsernames} : java.lang.String
Query Parameter {xp} : int
*/
DevBitsService.addToTwitchUser = function(amount, twitchUsernames, xp, successCallback, errorCallback){
$http({
method: 'PUT',
url: '/v1/devbits/' + twitchUsernames + '/' + amount + '',
data: {xp : xp}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

DevBitsService.http.addToTwitchUser = function(amount, twitchUsernames, xp, successCallback, errorCallback){
return $http({
method: 'PUT',
url: '/v1/devbits/' + twitchUsernames + '/' + amount + '',
data: {xp : xp}
})};return DevBitsService;
}]);
