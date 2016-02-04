angular.module('null.RestService', [])
   .factory('RestService', ['$http', function($http){
 var RestService = {};
RestService.http = {};

return RestService;
}]);
