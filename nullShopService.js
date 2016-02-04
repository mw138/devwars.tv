angular.module('null.ShopService', [])
   .factory('ShopService', ['$http', function($http){
 var ShopService = {};
ShopService.http = {};

/*
Required Role : PENDING
*/
ShopService.purchaseCustomAvatar = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/shop/purchase/custom_avatar',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

ShopService.http.purchaseCustomAvatar = function(successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/shop/purchase/custom_avatar',
params: {}
})};return ShopService;
}]);
