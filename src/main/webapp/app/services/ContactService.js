angular.module('app.ContactService', [])
   .factory('ContactService', ['$http', function($http){
 var ContactService = {};
ContactService.http = {};

/*
Query Parameter {name} : java.lang.String
Query Parameter {text} : java.lang.String
Query Parameter {type} : java.lang.String
Query Parameter {email} : java.lang.String
*/
ContactService.create = function(name, text, type, email, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/contact/create',
params: {name : name,text : text,type : type,email : email}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

ContactService.http.create = function(name, text, type, email, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/contact/create',
params: {name : name,text : text,type : type,email : email}
})};return ContactService;
}]);
