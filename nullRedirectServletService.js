angular.module('null.RedirectServletService', [])
   .factory('RedirectServletService', ['$http', function($http){
 var RedirectServletService = {};
RedirectServletService.http = {};

/*
Required Role : NONE
*/
RedirectServletService.index = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '//',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

RedirectServletService.http.index = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '//',
params: {}
})};/*
*/
RedirectServletService.about = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/rules',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

RedirectServletService.http.about = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/rules',
params: {}
})};/*
Path Variable {gameID} : int
Path Variable {seasonID} : int
*/
RedirectServletService.getGamePage = function(gameID, seasonID, successCallback, errorCallback){
$http({
method: 'GET',
url: '//s' + seasonID + '/' + gameID + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

RedirectServletService.http.getGamePage = function(gameID, seasonID, successCallback, errorCallback){
return $http({
method: 'GET',
url: '//s' + seasonID + '/' + gameID + '',
params: {}
})};/*
Query Parameter {requiredRole} : java.lang.String
*/
RedirectServletService.unauth = function(requiredRole, successCallback, errorCallback){
$http({
method: 'GET',
url: '//unauth',
params: {requiredRole : requiredRole}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

RedirectServletService.http.unauth = function(requiredRole, successCallback, errorCallback){
return $http({
method: 'GET',
url: '//unauth',
params: {requiredRole : requiredRole}
})};/*
*/
RedirectServletService.codeView = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '//codeview',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

RedirectServletService.http.codeView = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '//codeview',
params: {}
})};/*
Path Variable {slug} : java.lang.String
*/
RedirectServletService.getFontAwesome = function(slug, successCallback, errorCallback){
$http({
method: 'GET',
url: '//font/' + slug:.+ + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

RedirectServletService.http.getFontAwesome = function(slug, successCallback, errorCallback){
return $http({
method: 'GET',
url: '//font/' + slug:.+ + '',
params: {}
})};return RedirectServletService;
}]);
