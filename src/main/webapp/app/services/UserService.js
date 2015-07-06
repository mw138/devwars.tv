angular.module('app.UserService', [])
   .factory('UserService', ['$http', function($http){
 var UserService = {};

/*
Required Role : PENDING
*/
UserService.user = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/',
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
Query Parameter {password} : java.lang.String
Query Parameter {username} : java.lang.String
*/
UserService.login = function(password, username, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/login',
params: {password : password,username : username}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : PENDING
*/
UserService.logout = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/logout',
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
Query Parameter {captchaResponse} : java.lang.String
Query Parameter {password} : java.lang.String
Query Parameter {referral} : int
Query Parameter {email} : java.lang.String
Query Parameter {username} : java.lang.String
*/
UserService.createUser = function(captchaResponse, password, email, username, referral, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/create',
params: {captchaResponse : captchaResponse,password : password,referral : referral,email : email,username : username}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : USER
Path Variable {id} : int
*/
UserService.getUser = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/' + id + '',
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
Required Role : ADMIN
Path Variable {id} : int
Query Parameter {xp} : double
Query Parameter {points} : double
*/
UserService.addPoints = function(id, xp, points, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/' + id + '/addpoints',
params: {xp : xp,points : points}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : NONE
Query Parameter {uid} : java.lang.String
*/
UserService.validateUser = function(uid, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/validate',
params: {uid : uid}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : PENDING
Query Parameter {file} : org.springframework.web.multipart.MultipartFile
*/
UserService.changeAvatar = function(file, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/changeavatar',
params: {file : file}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : PENDING
*/
UserService.getActivities = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/activity',
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
Required Role : PENDING
Query Parameter {company} : java.lang.String
Query Parameter {location} : java.lang.String
Query Parameter {url} : java.lang.String
Query Parameter {username} : java.lang.String
*/
UserService.updateInfo = function(company, location, url, username, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/updateinfo',
params: {company : company,location : location,url : url,username : username}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : PENDING
*/
UserService.appliedGames = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/appliedgames',
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
Path Variable {username} : java.lang.String
*/
UserService.getPublicUser = function(username, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/' + username + '/public',
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
Path Variable {username} : java.lang.String
*/
UserService.getUserAvatar = function(username, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/' + username + '/avatar',
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
Required Role : PENDING
Query Parameter {newEmail} : java.lang.String
Query Parameter {currentPassword} : java.lang.String
*/
UserService.changeEmail = function(newEmail, currentPassword, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/changeemail',
params: {newEmail : newEmail,currentPassword : currentPassword}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : PENDING
*/
UserService.getAvatar = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/avatar',
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
Required Role : ADMIN
Path Variable {id} : int
*/
UserService.deleteUser = function(id, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/' + id + '/delete',
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
Required Role : PENDING
Query Parameter {newPassword} : java.lang.String
Query Parameter {currentPassword} : java.lang.String
*/
UserService.changePassword = function(newPassword, currentPassword, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/changepassword',
params: {newPassword : newPassword,currentPassword : currentPassword}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : PENDING
*/
UserService.claimTwitch = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/claimtwitch',
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
Required Role : PENDING
*/
UserService.getBadges = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/badges',
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
Query Parameter {uid} : java.lang.String
Query Parameter {email} : java.lang.String
*/
UserService.testEmail = function(uid, email, successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/testemail',
params: {uid : uid,email : email}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : PENDING
*/
UserService.releaseUsername = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/releasetwitch',
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
Required Role : PENDING
*/
UserService.markNotificationsAsRead = function(notifications, successCallback, errorCallback){
$http({
method: 'POST',
url: '/v1/user/notifications/read',
data: {notifications : notifications}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

/*
Required Role : PENDING
*/
UserService.getUnreadNotifications = function(successCallback, errorCallback){
$http({
method: 'GET',
url: '/v1/user/notifications',
params: {}
})
.then(function(success){
successCallback(success)
},
function(error){
errorCallback(error)
});

};

return UserService;
}]);
