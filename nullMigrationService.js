angular.module('null.MigrationService', [])
   .factory('MigrationService', ['$http', function($http){
 var MigrationService = {};
MigrationService.http = {};

/*
Required Role : ADMIN
*/
MigrationService.migrate_primary_o_auth_to_connected_account = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/migration/migrate_primary_o_auth_to_connected_account',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

MigrationService.http.migrate_primary_o_auth_to_connected_account = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/migration/migrate_primary_o_auth_to_connected_account',
params: {}
})};return MigrationService;
}]);
