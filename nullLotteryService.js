angular.module('null.LotteryService', [])
   .factory('LotteryService', ['$http', function($http){
 var LotteryService = {};
LotteryService.http = {};

/*
Query Parameter {count} : java.lang.Integer
*/
LotteryService.purchaseTickets = function(count, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/lottery//purchase',
params: {count : count}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

LotteryService.http.purchaseTickets = function(count, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/lottery//purchase',
params: {count : count}
})};return LotteryService;
}]);
