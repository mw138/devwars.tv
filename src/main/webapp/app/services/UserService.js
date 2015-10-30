angular.module('app.UserService', [])
    .factory('UserService', ['$http', function($http){
        var UserService = {};
        UserService.http = {};

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

        UserService.http.user = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/',
                params: {}
            })};/*
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

        UserService.http.markNotificationsAsRead = function(notifications, successCallback, errorCallback){
            return $http({
                method: 'POST',
                url: '/v1/user/notifications/read',
                data: {notifications : notifications}
            })};/*
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

        UserService.http.getUnreadNotifications = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/notifications',
                params: {}
            })};/*
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

        UserService.http.login = function(password, username, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/login',
                params: {password : password,username : username}
            })};/*
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

        UserService.http.logout = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/logout',
                params: {}
            })};/*
         Query Parameter {captchaResponse} : java.lang.String
         Query Parameter {password} : java.lang.String
         Query Parameter {referral} : java.lang.String
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

        UserService.http.createUser = function(captchaResponse, password, email, username, referral, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/create',
                params: {captchaResponse : captchaResponse,password : password,referral : referral,email : email,username : username}
            })};/*
         Query Parameter {password} : java.lang.String
         Query Parameter {password_confirmation} : java.lang.String
         Query Parameter {user} : int
         Query Parameter {key} : java.lang.String
         */
        UserService.resetPassword = function(password, password_confirmation, user, key, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/user/resetpassword',
                data: {password : password,password_confirmation : password_confirmation,user : user,key : key}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserService.http.resetPassword = function(password, password_confirmation, user, key, successCallback, errorCallback){
            return $http({
                method: 'POST',
                url: '/v1/user/resetpassword',
                data: {password : password,password_confirmation : password_confirmation,user : user,key : key}
            })};/*
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

        UserService.http.appliedGames = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/appliedgames',
                params: {}
            })};/*
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

        UserService.http.deleteUser = function(id, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/' + id + '/delete',
                params: {}
            })};/*
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

        UserService.http.validateUser = function(uid, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/validate',
                params: {uid : uid}
            })};/*
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

        UserService.http.getActivities = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/activity',
                params: {}
            })};/*
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

        UserService.http.changePassword = function(newPassword, currentPassword, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/changepassword',
                params: {newPassword : newPassword,currentPassword : currentPassword}
            })};/*
         Required Role : USER
         Query Parameter {username} : java.lang.String
         */
        UserService.searchUsers = function(username, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/user/search',
                params: {username : username}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserService.http.searchUsers = function(username, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/search',
                params: {username : username}
            })};/*
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

        UserService.http.changeAvatar = function(file, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/changeavatar',
                params: {file : file}
            })};/*
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

        UserService.http.updateInfo = function(company, location, url, username, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/updateinfo',
                params: {company : company,location : location,url : url,username : username}
            })};/*
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

        UserService.http.getBadges = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/badges',
                params: {}
            })};/*
         Required Role : ADMIN
         */
        UserService.hello = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/user/permission',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserService.http.hello = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/permission',
                params: {}
            })};/*
         Required Role : USER
         */
        UserService.getMyTeamInvites = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/user/teaminvites',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserService.http.getMyTeamInvites = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/teaminvites',
                params: {}
            })};/*
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

        UserService.http.changeEmail = function(newEmail, currentPassword, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/changeemail',
                params: {newEmail : newEmail,currentPassword : currentPassword}
            })};/*
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

        UserService.http.testEmail = function(uid, email, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/testemail',
                params: {uid : uid,email : email}
            })};/*
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

        UserService.http.releaseUsername = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/releasetwitch',
                params: {}
            })};/*
         Required Role : USER
         */
        UserService.getMyTeam = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/user/myteam',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserService.http.getMyTeam = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/myteam',
                params: {}
            })};/*
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

        UserService.http.getUserAvatar = function(username, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/' + username + '/avatar',
                params: {}
            })};/*
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

        UserService.http.getAvatar = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/avatar',
                params: {}
            })};/*
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

        UserService.http.getPublicUser = function(username, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/' + username + '/public',
                params: {}
            })};/*
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

        UserService.http.claimTwitch = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/claimtwitch',
                params: {}
            })};/*
         Required Role : USER
         */
        UserService.getOwnedTeam = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/user/ownedteam',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserService.http.getOwnedTeam = function(successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/ownedteam',
                params: {}
            })};/*
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

        UserService.http.getUser = function(id, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/' + id + '',
                params: {}
            })};/*
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

        UserService.http.addPoints = function(id, xp, points, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/' + id + '/addpoints',
                params: {xp : xp,points : points}
            })};/*
         Query Parameter {email} : java.lang.String
         */
        UserService.initResetPassword = function(email, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/user/initreset',
                params: {email : email}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserService.http.initResetPassword = function(email, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/user/initreset',
                params: {email : email}
            })};return UserService;
    }]);
