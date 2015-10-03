angular.module('app.TeamService', [])
   .factory('TeamService', ['$http', function($http){
 var TeamService = {};
TeamService.http = {};

/*
Required Role : ADMIN
Path Variable {id} : int
*/
TeamService.uploadSite = function(id, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/team/' + id + '/upload',
data: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

TeamService.http.uploadSite = function(id, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/team/' + id + '/upload',
data: {}
})};return TeamService;
}]);
