angular.module('app.BlogService', [])
   .factory('BlogService', ['$http', function($http){
 var BlogService = {};

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

/*
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

/*
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

/*
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

/*
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

return BlogService;
}]);
