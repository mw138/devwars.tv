angular.module('app.TeamService', [])
   .factory('TeamService', ['$http', function($http){
 var TeamService = {};

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

return TeamService;
}]);
