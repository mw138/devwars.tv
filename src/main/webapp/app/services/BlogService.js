angular.module('app.BlogService', [])
   .factory('BlogService', ['$http', function($http){
 var BlogService = {};
BlogService.http = {};

/*
Required Role : BLOGGER
*/
BlogService.createBlog = function(post, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/blog/create',
params: {post : post}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

BlogService.http.createBlog = function(post, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/blog/create',
params: {post : post}
})};/*
Required Role : BLOGGER
Path Variable {id} : int
*/
BlogService.deleteBlog = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/blog/' + id + '/delete',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

BlogService.http.deleteBlog = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/blog/' + id + '/delete',
params: {}
})};/*
Path Variable {title} : java.lang.String
*/
BlogService.getBlog = function(title, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/blog/' + title + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

BlogService.http.getBlog = function(title, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/blog/' + title + '',
params: {}
})};/*
Query Parameter {month} : java.lang.Integer
Query Parameter {year} : java.lang.Integer
Query Parameter {day} : java.lang.Integer
*/
BlogService.allPosts = function(month, year, day, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/blog/all',
params: {month : month,year : year,day : day}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

BlogService.http.allPosts = function(month, year, day, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/blog/all',
params: {month : month,year : year,day : day}
})};/*
Required Role : BLOGGER
Path Variable {id} : int
*/
BlogService.updateBlog = function(id, post, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/blog/' + id + '/update',
params: {post : post}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

BlogService.http.updateBlog = function(id, post, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/blog/' + id + '/update',
params: {post : post}
})};return BlogService;
}]);
