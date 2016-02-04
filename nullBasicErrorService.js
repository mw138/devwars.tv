angular.module('null.BasicErrorService', [])
   .factory('BasicErrorService', ['$http', function($http){
 var BasicErrorService = {};
BasicErrorService.http = {};

/*
*/
BasicErrorService.error = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '$' + error.path:/error + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

BasicErrorService.http.error = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '$' + error.path:/error + '',
params: {}
})};/*
*/
BasicErrorService.errorHtml = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '$' + error.path:/error + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

BasicErrorService.http.errorHtml = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '$' + error.path:/error + '',
params: {}
})};return BasicErrorService;
}]);
