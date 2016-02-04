angular.module('null.UserTeamService', [])
   .factory('UserTeamService', ['$http', function($http){
 var UserTeamService = {};
UserTeamService.http = {};

/*
Path Variable {id} : int
*/
UserTeamService.getTeam = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/' + id + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.getTeam = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/' + id + '',
params: {}
})};/*
Path Variable {id} : int
Query Parameter {image} : org.springframework.web.multipart.MultipartFile
*/
UserTeamService.changeAvatar = function(id, image, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/teams/' + id + '/avatar',
data: {image : image}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.changeAvatar = function(id, image, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/teams/' + id + '/avatar',
data: {image : image}
})};/*
Required Role : PENDING
Path Variable {id} : int
*/
UserTeamService.acceptInvite = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/' + id + '/invite/accept',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.acceptInvite = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/' + id + '/invite/accept',
params: {}
})};/*
Required Role : USER
Path Variable {id} : int
Path Variable {user} : int
*/
UserTeamService.kickUser = function(id, user, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/' + id + '/kick/' + user + '',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.kickUser = function(id, user, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/' + id + '/kick/' + user + '',
params: {}
})};/*
Required Role : PENDING
Path Variable {id} : int
Query Parameter {user} : int
*/
UserTeamService.invitePlayer = function(id, user, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/' + id + '/invite',
params: {user : user}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.invitePlayer = function(id, user, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/' + id + '/invite',
params: {user : user}
})};/*
Path Variable {id} : int
Query Parameter {count} : int
Query Parameter {page} : int
*/
UserTeamService.getHistory = function(id, count, page, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/' + id + '/history',
params: {count : count,page : page}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.getHistory = function(id, count, page, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/' + id + '/history',
params: {count : count,page : page}
})};/*
Query Parameter {newName} : java.lang.String
*/
UserTeamService.changeTeamName = function(newName, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/changeteamname',
params: {newName : newName}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.changeTeamName = function(newName, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/changeteamname',
params: {newName : newName}
})};/*
Path Variable {id} : int
*/
UserTeamService.getStatistics = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/' + id + '/statistics',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.getStatistics = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/' + id + '/statistics',
params: {}
})};/*
Required Role : USER
Query Parameter {name} : java.lang.String
Query Parameter {tag} : java.lang.String
*/
UserTeamService.createTeam = function(name, tag, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/teams/create',
data: {name : name,tag : tag}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.createTeam = function(name, tag, successCallback, errorCallback){
return $http({
method: 'POST',
url: '/v1/teams/create',
data: {name : name,tag : tag}
})};/*
Required Role : USER
Path Variable {id} : int
Query Parameter {name} : java.lang.String
Query Parameter {newOwner} : java.lang.Integer
*/
UserTeamService.deleteTeam = function(id, name, newOwner, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/' + id + '/delete',
params: {name : name,newOwner : newOwner}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.deleteTeam = function(id, name, newOwner, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/' + id + '/delete',
params: {name : name,newOwner : newOwner}
})};/*
Required Role : USER
Path Variable {id} : int
*/
UserTeamService.leaveTeam = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/' + id + '/leave',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.leaveTeam = function(id, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/' + id + '/leave',
params: {}
})};/*
Query Parameter {name} : java.lang.String
Query Parameter {tag} : java.lang.String
*/
UserTeamService.checkTeamInformation = function(name, tag, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/teams/check',
params: {name : name,tag : tag}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

UserTeamService.http.checkTeamInformation = function(name, tag, successCallback, errorCallback){
return $http({
method: 'GET',
url: '/v1/teams/check',
params: {name : name,tag : tag}
})};return UserTeamService;
}]);
