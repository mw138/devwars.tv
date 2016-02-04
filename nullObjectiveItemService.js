angular.module('null.ObjectiveItemService', [])
   .factory('ObjectiveItemService', ['$http', function($http){
 var ObjectiveItemService = {};
ObjectiveItemService.http = {};

/*
Required Role : ADMIN
Query Parameter {objective} : java.lang.String
*/
ObjectiveItemService.createObjective = function(objective, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/objective/create',
params: {objective : objective}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

ObjectiveItemService.http.createObjective = function(objective, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/objective/create',
params: {objective : objective}
})};/*
Required Role : ADMIN
Path Variable {id} : int
*/
ObjectiveItemService.getObjective = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/objective/' + id + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

ObjectiveItemService.http.getObjective = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/objective/' + id + '',
params: {}
})};/*
Path Variable {id} : int
*/
ObjectiveItemService.deleteObjective = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/objective/' + id + '/delete',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

ObjectiveItemService.http.deleteObjective = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/objective/' + id + '/delete',
params: {}
})};return ObjectiveItemService;
}]);
