angular.module('app.InfoService', [])
   .factory('InfoService', ['$http', function($http){
 var InfoService = {};
InfoService.http = {};

/*
*/
InfoService.bitsLeaderboard = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/info/bitsleaderboard',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

InfoService.http.bitsLeaderboard = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/info/bitsleaderboard',
params: {}
})};/*
*/
InfoService.allInfo = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/info/',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

InfoService.http.allInfo = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/info/',
params: {}
})};/*
*/
InfoService.xpLeaderboard = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/info/xpleaderboard',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

InfoService.http.xpLeaderboard = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/info/xpleaderboard',
params: {}
})};/*
Query Parameter {page} : int
*/
InfoService.leaderboard = function(page, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/info/leaderboard',
params: {page : page}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

InfoService.http.leaderboard = function(page, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/info/leaderboard',
params: {page : page}
})};return InfoService;
}]);
