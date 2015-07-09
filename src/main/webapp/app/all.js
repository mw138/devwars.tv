var app = angular.module('app', [
// modules
    'app.coming',
    'app.AuthService',
    'app.header',
    'app.home',
    'app.signup',
    'app.gameControlPanel',
    'app.GameService',
    'app.BlogService',
    'app.InfoService',
    'app.UserService',
    'app.WarriorService',
    'app.ContactService',
    'app.games',
    'app.gameSignupConfirm',
    'app.editPlayerDialog',
    'app.addPlayerDialog',
    'app.addSelectedPlayerDialog',
    'app.addBlogPostDialog',
    'app.InputDialog',
    'app.devwarsToast',
    'app.toastService',
    'app.BadgeService',
    'app.blog',
    'app.user',
    'pickadate',
    'app.contact',
    'app.about',
    'app.dashboard',
    'app.badges',
    'app.warriorReg',
    'app.DialogService',
    'app.settings',
    'app.help',
    'app.liveGame',
    'app.verify',
    'app.enterDirective',
    'app.subscribeDirective',
    'app.shop',
    'app.OAuthDirective',
    'app.sidebar',
    'app.editAvatarImage',
    'app.fileread',
    'app.rank',
    'app.profile',
    'app.dashnav',
    'app.confirmDialog',
    'app.leaderboards',
    'app.modCP',

    //Poll
    'LivePoll-Client',
    'LivePoll-Display',
    'Progress',
    'Tooltip',

    //dependencies
    'ngCookies',
    'ngMaterial',
    'vcRecaptcha',
    'textAngular',
    'n3-pie-chart',
    'ngImgCrop'
]);

app.config(['$urlRouterProvider', '$httpProvider', '$locationProvider', function ($urlRouterProvider, $httpProvider, $locationProvider) {
    // all page specific routes are in their js file
    $urlRouterProvider.otherwise('/');

    $httpProvider.defaults.transformResponse = function (response) {
        try {
            return JSON.parse(response);
        } catch (exception) {
            return response;
        }
    };

    $httpProvider.defaults.transformRequest = function (obj) {
        var str = [];
        for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        return str.join("&");
    };

    $httpProvider.defaults.headers.post['Content-Type'] = "application/x-www-form-urlencoded";

    $locationProvider.html5Mode(true);
}]);

app.filter("reverse", function () {
    return function (data) {
        return data.reverse();
    }
});

app.filter("players", function () {
    return _.memoize(function (data) {
        var langs = ["html", "css", "js"];

        data.sort(function (a) {
            return langs.indexOf(a.language.toLowerCase());
        });

        return data;
    })
});
angular.module("app.enterDirective", [])
    .directive("ngEnter", function () {
        return function(scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if(event.which === 13) {
                    scope.$apply(function (){
                        scope.$eval(attrs.ngEnter);
                    });

                    event.preventDefault();
                }
            });
        }
    });
angular.module("app.fileread", [])
    .directive('fileread', function () {
        return {
            restrict: "A",

            scope: {
                "fileread": "="
            },

            link: function ($scope, $element) {
                $element.bind("change", function ($event) {
                    var reader = new FileReader();

                    reader.onload = function (readevent) {
                        $scope.$apply(function ($scope) {
                            $scope.fileread = readevent.target.result;
                        })
                    };

                    reader.readAsDataURL($event.target.files[0]);
                })
            }
        }
    });
angular.module("app.OAuthDirective", [])
    .directive("oauth", function () {
        return {
            restrict: "A",

            controller: function ($scope, $element, $attrs) {
                $element.bind("click", function () {

                    if($attrs.provider) {
                        location.href = "/v1/oauth/" + $attrs.provider;
                    }

                });
            }
        }
    });
angular.module("app.subscribeDirective", [])
    .directive("subscribe", function () {
        return {
            restrict: "A",
            controller: function(AuthService, $element) {
                $element.bind("click", function () {
                    var link = "//devwars.us10.list-manage.com/subscribe/post?u=0719cdf11a95678dd04e5db33&id=02e4f24ca1";

                    if (AuthService.user && AuthService.user.email) {
                        link += "&EMAIL=" +AuthService.user.email;
                    }

                    window.open(link, "_blank");
                })
            }
        };
    });
/**
 * Created by Terence on 3/22/2015.
 */
angular.module("app.AuthService", [])
    .factory("AuthService", ["$http", function ($http) {
        var AuthService = {};

        AuthService.callbacks = [];

        AuthService.init = function () {
            $http({
                method: "GET",
                url: "/v1/user/"
            })
                .then(function (success) {
                    console.log(success);
                    AuthService.setUser(success.data);

                    for (var i = 0; i < AuthService.callbacks.length; i++) {
                        AuthService.callbacks[i](success.data);
                    }
                }, function (error) {

                });
        };

        AuthService.login = function (credentials, successCallback, errorCallback) {
            return $http({
                method: "GET",
                url: "/v1/user/login",
                params: credentials
            })
                .then(function (success) {
                    location.href = "/dashboard";
                }, function (error) {
                    console.log(error);
                    errorCallback(error);
                });
        };

        AuthService.signup = function (credentials, successCallback, errorCallback) {
            return $http({
                method: "POST",
                url: "/v1/user/create",
                params: credentials
            })
                .then(function (success) {
                    successCallback(success);
                }, function (error) {
                    errorCallback(error);
                })
        }

        AuthService.logout = function () {
            return $http({
                method: "GET",
                url: "/v1/user/logout"
            })
                .then(function (success) {
                    location.href = "/";
                }, function (error) {
                    console.log(error);
                })
        }

        AuthService.setUser = function (user) {
            AuthService.user = user;
        };

        AuthService.isAdmin = function () {
            return AuthService.user ? AuthService.user.role === "ADMIN" : false;
        };

        AuthService.hasProvider = function (provider) {
            if(!AuthService.user || !AuthService.user.connectedAccounts) return false;

            for(var key in AuthService.user.connectedAccounts) {
                var account = AuthService.user.connectedAccounts[key];

                if(account.provider.toUpperCase() === provider.toUpperCase() && account.disconnected == false) return true;
            }

            return false;
        };

        AuthService.hasConnectedToProvider = function (provider) {
            if(!AuthService.user || !AuthService.user.connectedAccounts) return false;

            for(var key in AuthService.user.connectedAccounts) {
                var account = AuthService.user.connectedAccounts[key];

                if(account.provider.toUpperCase() === provider.toUpperCase()) return true;
            }

            return false;
        };

        return AuthService;
    }]);

angular.module('app.BadgeService', [])
    .factory('BadgeService', ['$http', function($http){
        var BadgeService = {};

        /*
         */
        BadgeService.getAll = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/badge/all',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        return BadgeService;
    }]);

angular.module('app.BlogService', [])
    .factory('BlogService', ['$http', function($http){
        var BlogService = {};

        /*
         Required Role : ADMIN
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
         Path Variable {id} : int
         */
        BlogService.getBlog = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/' + id + '',
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
         Query Parameter {image_url} : java.lang.String
         Query Parameter {description} : java.lang.String
         Query Parameter {text} : java.lang.String
         Query Parameter {title} : java.lang.String
         */
        BlogService.updateBlog = function(id, image_url, description, text, title, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/' + id + '/update',
                params: {image_url : image_url,description : description,text : text,title : title}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        BlogService.allPosts = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/all',
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
         Query Parameter {image_url} : java.lang.String
         Query Parameter {description} : java.lang.String
         Query Parameter {text} : java.lang.String
         Query Parameter {title} : java.lang.String
         */
        BlogService.createBlog = function(image_url, description, text, title, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/create',
                params: {image_url : image_url,description : description,text : text,title : title}
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

angular.module('app.ContactService', [])
    .factory('ContactService', ['$http', function($http){
        var ContactService = {};

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

        return ContactService;
    }]);

angular.module("app.DialogService", [])
    .factory("DialogService", ["GameService", "ToastService", "$mdDialog", "AuthService", "$filter", "$state", function (GameService, ToastService, $mdDialog, AuthService, $filter, $state) {
        var DialogService = {};

        DialogService.applyForNearestGame = function ($event) {
            GameService.nearestGame(function (nearestGameSuccess) {
                DialogService.applyForGame(nearestGameSuccess.data, $event);
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa fa-exclamation-circle", "Error", "No upcoming games");
            })
        };

        DialogService.applyForCurrentGame = function ($event) {
            GameService.currentGame(function (nearestGameSuccess) {
                DialogService.applyForGame(nearestGameSuccess.data, $event);
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa fa-exclamation-circle", "Error", "No upcoming games");
            })
        };

        DialogService.applyForGame = function (game, $event, successCallback, errorCallback) {
            if(AuthService.user && AuthService.user.warrior) {
                $mdDialog.show({
                    controller: "GameSignupConfirmDialogController",
                    templateUrl: "app/components/dialogs/confirmGameSignupDialog/confirmGameSignupDialogView.html",
                    targetEvent: $event,

                    locals: {
                        game: game
                    }
                })
                    .then(function (success) {
                        GameService.signupForGame(success.game.id, function (signupSuccess) {
                            AuthService.init();
                            ToastService.showDevwarsToast("fa-calendar", "Game applied for", $filter("date")(game.timestamp, 'medium'));
                        }, function (error) {
                            ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                        })
                    }, null);
            } else
            {
                if(!AuthService.user) {
                    DialogService.login($event);
                } else {
                    $state.go("warriorReg");
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Please signup to be a warrior");
                }
            }
        };

        DialogService.login = function ($event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/loginDialog/loginDialogView.html",
                controller: "LoginDialogController",
                targetEvent: $event,

                clickOutsideToClose: true,
                escapeToClose: true
            })
                .then(function (success) {

                }, function (error) {
                    if(error && error.register) {
                        DialogService.signup($event);
                    }
                });
        };

        DialogService.signup = function ($event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/signupDialog/signupDialogView.html",
                controller: "SignupDialogController",
                targetEvent: $event,

                clickOutsideToClose: true,
                escapeToClose: true
            })
                .then(function (success) {

                }, function (error) {
                    if(error && error.login) {
                        DialogService.login($event);
                    }
                });
        };

        DialogService.getInputWithMessage = function (title, message, $event, callback) {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/inputDialog/inputDialogView.html",
                controller: "InputDialogController",
                targetEvent: $event,

                locals: {
                    title: title,
                    message: message
                }

            })
                .then(function (success) {
                    callback(success);
                }, function (error) {

                })
        };

        DialogService.showConfirmDialog = function (title, message, yes, no, $event) {
            return $mdDialog.show({
                templateUrl: "app/components/dialogs/confirmDialog/confirmDialogView.html",
                controller: "ConfirmDialogController",
                targetEvent: $event,

                locals: {
                    title: title,
                    message: message,
                    yes: yes,
                    no: no
                }

            });
        };

        return DialogService;
    }]);
angular.module('app.GameService', [])
    .factory('GameService', ['$http', function($http){
        var GameService = {};

        /*
         */
        GameService.resetVeteranGames = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/resetVeteranGames',
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
         Path Variable {teamID} : int
         Path Variable {id} : int
         Path Variable {playerID} : int
         Query Parameter {json} : java.lang.String
         */
        GameService.editPlayer = function(teamID, id, playerID, json, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/team/' + teamID + '/player/' + playerID + '/edit',
                params: {json : json}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        GameService.pastGames = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/pastgames',
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
         Path Variable {teamID} : int
         Path Variable {id} : int
         Query Parameter {language} : java.lang.String
         */
        GameService.addPlayer = function(teamID, id, language, user, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/game/' + id + '/team/' + teamID + '/add',
                data: {language : language,user : user}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        GameService.upcomingGames = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/upcoming',
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
         Query Parameter {offset} : int
         Query Parameter {count} : int
         */
        GameService.allGames = function(offset, count, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/',
                params: {offset : offset,count : count}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        GameService.latestGame = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/latestgame',
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
         Query Parameter {winner} : int
         */
        GameService.endGame = function(id, winner, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/endgame',
                params: {winner : winner}
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
         Path Variable {playerID} : int
         */
        GameService.removePlayer = function(id, playerID, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/player/' + playerID + '/remove',
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
         */
        GameService.currentGame = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/currentgame',
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
         Query Parameter {json} : java.lang.String
         */
        GameService.editGame = function(id, json, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/game/' + id + '/update',
                data: {json : json}
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
        GameService.pendingPlayers = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/pendingplayers',
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
         */
        GameService.forfeitUser = function(player, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/game/forfeituser',
                data: {player : player}
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
         Query Parameter {name} : java.lang.String
         Query Parameter {time} : long
         */
        GameService.createGame = function(name, time, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/create',
                params: {name : name,time : time}
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
        GameService.activateGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/activate',
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
         Required Role : USER
         Path Variable {id} : int
         */
        GameService.signupForGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/signup',
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
         Path Variable {teamID} : int
         Path Variable {id} : int
         Query Parameter {code} : int
         Query Parameter {func} : int
         Query Parameter {design} : int
         */
        GameService.addVotes = function(teamID, id, code, func, design, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/team/' + teamID + '/addvotes',
                params: {code : code,func : func,design : design}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         */
        GameService.nearestGame = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/nearestgame',
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
         Path Variable {teamID} : int
         Path Variable {id} : int
         Query Parameter {xp} : int
         Query Parameter {points} : int
         */
        GameService.addPointsToTeam = function(teamID, id, xp, points, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/team/' + teamID + '/addpoints',
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
         Path Variable {id} : int
         */
        GameService.getGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '',
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
        GameService.deleteGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/delete',
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
        GameService.deactivateGame = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/game/' + id + '/deactivate',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        return GameService;
    }]);

angular.module('app.InfoService', [])
    .factory('InfoService', ['$http', function($http){
        var InfoService = {};

        /*
         */
        InfoService.allInfo = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/info/',
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
         */
        InfoService.xpLeaderboard = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/info/xpleaderboard',
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
         */
        InfoService.bitsLeaderboard = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/info/bitsleaderboard',
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
         Query Parameter {page} : int
         */
        InfoService.leaderboard = function(page, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/info/leaderboard',
                params: {page : page}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        return InfoService;
    }]);

angular.module('app.MailingListService', [])
    .factory('MailingListService', ['$http', function($http){
        var MailingListService = {};

        return MailingListService;
    }]);

/**
 * Created by Terence on 4/6/2015.
 */
angular.module("app.toastService", [])
    .factory("ToastService", ["$mdToast", function ($mdToast) {
        var ToastService = {};

        ToastService.showDevwarsToast = function (icon, title, message, delay) {
            return $mdToast.show({
                templateUrl: "/app/components/toasts/devwarsToast/devwarsToastView.html",
                controller: "DevWarsToastController",

                locals: {
                    faIcon: icon,
                    title: title,
                    message: message
                },

                delay: delay ? delay : 3000
            });
        }

        ToastService.showDevwarsErrorToast = function (icon, title, message, delay) {
            return $mdToast.show({
                templateUrl: "/app/components/toasts/devwarsToast/devwarsErrorToastView.html",
                controller: "DevWarsToastController",

                locals: {
                    faIcon: icon,
                    title: title,
                    message: message
                },

                delay: delay ? delay : 3000
            });
        }

        return ToastService;
    }]);
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

angular.module('app.WarriorService', [])
    .factory('WarriorService', ['$http', function($http){
        var WarriorService = {};

        /*
         Required Role : PENDING
         Query Parameter {cssRate} : int
         Query Parameter {year} : int
         Query Parameter {about} : java.lang.String
         Query Parameter {htmlRate} : int
         Query Parameter {firstName} : java.lang.String
         Query Parameter {month} : int
         Query Parameter {jsRate} : int
         Query Parameter {c9Name} : java.lang.String
         Query Parameter {location} : java.lang.String
         Query Parameter {company} : java.lang.String
         Query Parameter {favFood} : java.lang.String
         Query Parameter {favTool} : java.lang.String
         Query Parameter {day} : int
         */
        WarriorService.register = function(cssRate, year, about, htmlRate, firstName, month, jsRate, c9Name, location, favFood, favTool, day, company, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/warrior/register',
                params: {cssRate : cssRate,year : year,about : about,htmlRate : htmlRate,firstName : firstName,month : month,jsRate : jsRate,c9Name : c9Name,location : location,company : company,favFood : favFood,favTool : favTool,day : day}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        return WarriorService;
    }]);

/**
 * Created by Terence on 3/21/2015.
 */
angular.module("app.header", ['ngCookies'])
    .controller("HeaderController", ["$scope", "$mdDialog", "AuthService", "GameService", "$filter", "DialogService", "$state", "$cookies", "$cookieStore", "UserService", "ToastService", function ($scope, $mdDialog, AuthService, GameService, $filter, DialogService, $state, $cookies, $cookieStore, UserService, ToastService) {

        AuthService.init();
        $scope.AuthService = AuthService;
        $scope.$state = $state;

        $scope.hasInit = false;

        $scope.DialogService = DialogService;
        $scope.$cookies = $cookies;

        GameService.currentGame(function (success) {
            $scope.currentGame = success.data;

            $scope.currentGameMessage = "We're live right now : " + $filter('date')($scope.currentGame.timestamp, "shortTime") + " on " + $filter('date')($scope.currentGame.timestamp, "longDate");
        }, function (error) {
            $scope.currentGame = {}

            GameService.nearestGame(function (success) {
                $scope.nearestGame = success.data;

                $scope.nearestGameMessage = "Next Game @ " + $filter('date')($scope.nearestGame.timestamp, "shortTime") + " on " + $filter('date')($scope.nearestGame.timestamp, "longDate");
            }, function (error) {
                $scope.currentGame = {}
            });
        });

        $scope.logout = function () {
            AuthService.logout();
        };

        UserService.getUnreadNotifications(function (success) {
            success.data.forEach(function (a, index) {
                setTimeout(function () {
                    ToastService.showDevwarsToast("fa-check-circle", "Notification", a.notificationText);
                }, index * 3000)
            });

            UserService.markNotificationsAsRead(JSON.stringify(success.data), angular.noop, angular.noop);
        }, angular.noop);
    }]);
/**
 * Created by Terence on 3/21/2015.
 */
angular.module("app.footer", [])
    .controller("footerController", ["$scope", "$mdDialog", "DialogService", function ($scope, $mdDialog, DialogService) {
        $scope.DialogService = DialogService;
    }])



var _twitchUsername = 'b3zman41';
var _appServer = 'http://dragon.umryn.net:22050';


(function(angular, undefined) {
    var angularSpectrumColorpicker = angular.module("angularSpectrumColorpicker", []);
    (function(undefined) {
        angularSpectrumColorpicker.directive("spectrumColorpicker", function() {
            return {
                restrict: "E",
                require: "ngModel",
                scope: false,
                replace: true,
                template: '<span><input class="input-small" /></span>',
                link: function($scope, $element, attrs, $ngModel) {
                    var $input = $element.find("input");
                    var fallbackValue = $scope.$eval(attrs.fallbackValue);
                    var format = $scope.$eval(attrs.format) || undefined;

                    function setViewValue(color) {
                        var value = fallbackValue;
                        if (color) {
                            value = color.toString(format)
                        } else {
                            if (angular.isUndefined(fallbackValue)) {
                                value = color
                            }
                        }
                        $ngModel.$setViewValue(value)
                    }
                    var onChange = function(color) {
                        $scope.$apply(function() {
                            setViewValue(color)
                        })
                    };
                    var onToggle = function() {
                        $input.spectrum("toggle");
                        return false
                    };
                    var options = angular.extend({
                        color: $ngModel.$viewValue,
                        change: onChange,
                        move: onChange,
                        hide: onChange
                    }, $scope.$eval(attrs.options));
                    if (attrs.triggerId) {
                        angular.element(document.body).on("click", "#" + attrs.triggerId, onToggle)
                    }
                    $ngModel.$render = function() {
                        $input.spectrum("set", $ngModel.$viewValue || "")
                    };
                    if (options.color) {
                        $input.spectrum("set", options.color || "");
                        setViewValue(options.color)
                    }
                    $input.spectrum(options);
                    $scope.$on("$destroy", function() {
                        $input.spectrum("destroy")
                    })
                }
            }
        })
    })()
})(window.angular);
(function() {
    angular.module("LivePoll-Client", ["Progress", "Tooltip", "angularSpectrumColorpicker"]).config(function($interpolateProvider) {
    })
})();
(function() {
    angular.module("LivePoll-Client").controller("LivePollClientCtrl", LivePollClientCtrl);
    LivePollClientCtrl.$inject = ["$rootScope", "$scope", "$timeout", "$interval"];

    function LivePollClientCtrl($rootScope, $scope, $timeout, $interval) {
        $scope.connected = false;
        $scope.throttled = false;
        $scope.user = null;
        $scope.defaultStyle = {
            bgColor: "rgba(0, 0, 0, .75)",
            barColor: "rgb(231, 163, 17)",
            barTextColor: "#f1f1f1",
            barTextShadowColor: "rgba(0, 0, 0, .5)",
            optionBgColor: "rgb(255, 255, 255)",
            optionTextColor: "#333333",
            optionTextShadowColor: "rgba(0, 0, 0, 0)",
            outerTextColor: "#f1f1f1",
            outerTextShadowColor: "rgba(0, 0, 0, .5)"
        };
        $scope.getDefaultStyle = function(styleRule) {
            if (typeof $scope.defaultStyle[styleRule] !== "undefined") {
                return $scope.defaultStyle[styleRule]
            }
        };
        $scope.resetStyles = function() {
            for (var styleRule in $scope.defaultStyle) {
                $scope.input[styleRule] = $scope.defaultStyle[styleRule]
            }
        };
        $scope.question = "";
        $scope.totalVotes = 0;
        $scope.options = [];
        var defaultInput = {
            bgColor: $scope.getDefaultStyle("bgColor"),
            barColor: $scope.getDefaultStyle("barColor"),
            barTextColor: $scope.getDefaultStyle("barTextColor"),
            barTextShadowColor: $scope.getDefaultStyle("barTextShadowColor"),
            optionBgColor: $scope.getDefaultStyle("optionBgColor"),
            optionTextColor: $scope.getDefaultStyle("optionTextColor"),
            optionTextShadowColor: $scope.getDefaultStyle("optionTextShadowColor"),
            outerTextColor: $scope.getDefaultStyle("outerTextColor"),
            outerTextShadowColor: $scope.getDefaultStyle("outerTextShadowColor"),
            options: [{
                text: "",
                keyword: ""
            }, {
                text: "",
                keyword: ""
            }],
            question: "",
            subMult: 1,
            subOnly: "false"
        };
        var originalInput = angular.copy(defaultInput);
        $scope.input = angular.copy(originalInput);
        $scope.updateStatus = function(connected, statusText) {
            $timeout(function() {
                $scope.connected = connected;
                $scope.status = statusText
            })
        };
        $scope.testInterval = null;
        $scope.testForm = function() {
            $timeout(function() {
                $interval.cancel($scope.testInterval);
                $scope.question = "Is this teh urn? (Fake Poll)";
                $scope.totalVotes = 100;
                $scope.options = [{
                    text: "Teh urn",
                    keyword: "urn",
                    count: 50
                }, {
                    text: "Not teh urn",
                    keyword: "noep",
                    count: 50
                }];
                $scope.testInterval = $interval(function() {
                    var randomBinary = Math.floor(Math.random() * 9);
                    (randomBinary > 0) ? $scope.options[1].count += 1: $scope.options[0].count += 1;
                    $scope.totalVotes += 1
                }, 150)
            })
        };
        $scope.testForm();
        $scope.percentage = function(count) {
            var percent = Number(count / $scope.totalVotes * 100).toFixed(2);
            return (isNaN(percent)) ? 0 : percent
        };
        $scope.closePoll = function() {
            $scope.throttleButtons();
            if (socket === undefined) {
                return
            }
            socket.emit("close_poll", $scope.user)
        };
        $scope.resetForm = function() {
            $scope.input.options.splice(2, $scope.input.options.length - 2);
            $scope.input = angular.copy(originalInput);
            $scope.pollForm.$setPristine()
        };
        $scope.addOption = function() {
            $timeout(function() {
                var numOptions = $scope.input.options.length;
                if (numOptions < 8) {
                    $scope.input.options.push({
                        text: "",
                        keyword: ""
                    })
                }
            })
        };
        $scope.removeOption = function() {
            $timeout(function() {
                var numOptions = $scope.input.options.length;
                var lastIndex = numOptions - 1;
                if (numOptions > 2) {
                    $scope.input.options.splice(lastIndex, 1)
                }
            })
        };
        $scope.submitForm = function(valid) {
            $scope.throttleButtons();
            if (valid && typeof socket !== "undefined") {
                var newPoll = {
                    options: $scope.input.options,
                    question: $scope.input.question,
                    subOnly: $scope.input.subOnly,
                    style: {},
                    token: _twitchToken
                };
                for (var styleRule in $scope.defaultStyle) {
                    if ($scope.defaultStyle.hasOwnProperty(styleRule)) {
                        var defaultStyle = $scope.defaultStyle[styleRule];
                        if (defaultStyle !== $scope.input[styleRule]) {
                            newPoll.style[styleRule] = $scope.input[styleRule]
                        }
                    }
                }
                socket.emit("create_poll", newPoll)
            }
        };
        $scope.throttleButtons = function() {
            $scope.throttled = true;
            $timeout(function() {
                $scope.throttled = false
            }, 2000)
        };
        $scope.incSubMult = function() {
            var value = Number($scope.input.subMult);
            if (value < 99) {
                $scope.input.subMult = value + 1
            }
        };
        $scope.decSubMult = function() {
            var value = Number($scope.input.subMult);
            if (value > 1) {
                $scope.input.subMult = value - 1
            }
        };
        if (typeof io === "undefined") {
            $scope.updateStatus(false, "Failed to load socket connection library... Application may be down. Please try reloading this page.");
            return
        }
        var socket = io(_appServer);
        socket.on("connect", function() {
            $scope.updateStatus(true, "Connected.");
            socket.emit("client_connect", {
                user: _twitchUsername,
                token: _twitchToken
            })
        });
        socket.on("disconnect", function() {
            $scope.updateStatus(false, "Disconnected, attempting to reconnect...")
        });
        socket.on("user", function(user) {
            if (user === null) {
                $scope.updateStatus(false, "Your session data does not match what Twitch is telling me. You should try logging out and logging in again.");
                return
            }
            $timeout(function() {
                $scope.user = user
            })
        });
        socket.on("poll", function(poll) {
            $interval.cancel($scope.testInterval);
            $timeout(function() {
                $scope.question = poll.question;
                $scope.totalVotes = poll.totalVotes;
                $scope.options = poll.options
            })
        });
        socket.on("poll_closed", function() {
            $scope.testForm()
        });
        socket.on("vote", function(id) {
            $timeout(function() {
                $scope.options[id].count += 1;
                $scope.totalVotes += 1
            })
        });
        socket.on("vote_switch", function(voteSwitch) {
            $timeout(function() {
                $scope.options[voteSwitch.from].count -= 1;
                $scope.options[voteSwitch.to].count += 1
            })
        })
    }
})();
(function() {
    angular.module("LivePoll-Display", ["ngAnimate", "Progress"]).config(function($interpolateProvider) {
    })
})();
(function() {
    angular.module("LivePoll-Display").controller("LivePollDisplayCtrl", LivePollDisplayCtrl);
    LivePollDisplayCtrl.$inject = ["$scope", "$window", "$interval", "$timeout"];

    function LivePollDisplayCtrl($scope, $window, $interval, $timeout) {
        $scope.closed = false;
        $scope.options = [];
        $scope.pollActive = false;
        $scope.question = "";
        $scope.style = {};
        $scope.totalVotes = 0;
        $scope.percentage = function(count) {
            var percent = Number(count / $scope.totalVotes * 100).toFixed(2);
            return (isNaN(percent)) ? 0 : percent
        };
        $scope.addColor = function() {};
        $scope.textShadow = function(value) {
            if (typeof value !== "undefined") {
                return "1px 1px 0 " + value
            }
            return {}
        };
        if ($window.innerHeight < 500 || $window.innerWidth < 500) {
            $scope.showWindowEnforcer = true
        }
        if (typeof io === "undefined") {
            return
        }
        var socket = io(_appServer);
        socket.on("connect", function() {
            socket.emit("client_connect", {
                user: _twitchUsername
            })
        });
        socket.on("poll", function(poll) {
            $timeout(function() {
                $scope.style.bgColor = poll.style.bgColor;
                $scope.style.barColor = poll.style.barColor;
                $scope.style.barTextColor = poll.style.barTextColor;
                $scope.style.barTextShadowColor = poll.style.barTextShadowColor;
                $scope.style.optionBgColor = poll.style.optionBgColor;
                $scope.style.optionTextColor = poll.style.optionTextColor;
                $scope.style.optionTextShadowColor = poll.style.optionTextShadowColor;
                $scope.style.outerTextColor = poll.style.outerTextColor;
                $scope.style.outerTextShadowColor = poll.style.outerTextShadowColor;
                $scope.question = poll.question;
                $scope.totalVotes = poll.totalVotes;
                $scope.options = poll.options;
                $scope.pollActive = true
            });
            console.log("poll received");
            console.log(poll)
        });
        socket.on("poll_closed", function() {
            console.log("poll closed");
            $timeout(function() {
                $scope.pollActive = false
            })
        });
        socket.on("vote", function(id) {
            $timeout(function() {
                $scope.options[id].count += 1;
                $scope.totalVotes += 1
            });
            console.log("vote received: %s", id)
        });
        socket.on("vote_switch", function(voteSwitch) {
            $timeout(function() {
                $scope.options[voteSwitch.from].count -= 1;
                $scope.options[voteSwitch.to].count += 1
            });
            console.log("vote switch received")
        })
    }
})();
(function() {
    angular.module("Progress", [])
})();
(function() {
    angular.module("Progress").directive("ngProgress", ngProgress);

    function ngProgress() {
        return {
            link: function(scope, element, attrs) {
                attrs.$observe("percent", function(val) {
                    element.css("width", val + "%")
                })
            }
        }
    }
})();
(function() {
    angular.module("Tooltip", [])
})();
(function() {
    angular.module("Tooltip").directive("ngTooltip", ngTooltip);
    ngTooltip.$inject = ["$document"];

    function ngTooltip($document) {
        return {
            link: function(scope, element, attrs) {
                var value = attrs.ngTooltip;
                var body = angular.element($document[0].body);
                var tooltip = angular.element('<div class="tooltip"></div>');
                tooltip.html(value);
                tooltip.css("display", "none");
                body.append(tooltip);
                element.hover(function() {
                    var epos = element.offset();
                    var ey = epos.top;
                    var ex = epos.left;
                    var ew = element.width();
                    var eh = element.height();
                    tooltip.css("left", ex + ew + "px");
                    tooltip.css("top", (ey + (eh / 2)) - 25 + "px");
                    tooltip.stop().fadeIn(200)
                }, function() {
                    tooltip.stop().fadeOut(200)
                })
            }
        }
    }
})();
angular.module("app.user", [])
    .controller("UserController", ["$scope", "UserService", "ToastService", function ($scope, UserService, ToastService) {
        $scope.user = null;

        $scope.updateUser = function () {
            UserService.getUser($scope.user.id, function (success) {
                $scope.user = success.data;
            }, function (error) {
                console.log(error);
            });
        }

        $scope.setUserId = function (id) {
            UserService.getUser(id, function (success) {
                $scope.user = success.data;
                console.log($scope.user);
            }, angular.noop);
        }

        $scope.setUser = function (user) {
            $scope.user = user;
            console.log($scope.user);
        }

        $scope.addPoints = function (points) {
            UserService.addPoints($scope.user.id, 0, points, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", (points > 0 ? "Added" : "Subtracted") + " points", (points > 0 ? "Added" : "Subtracted") + " " + points + " points");
                $scope.updateUser();
            }, angular.noop);
        }
    }]);
angular.module("app.dashnav", [])
    .directive("dashnav", function () {
        return {
            restrict: "E",
            templateUrl: "/app/directives/dashnavDirective/dashnavView.html",

            controller: function ($scope) {
                $scope.goToProfile = function (profile) {
                    location.href = "/profile?username=" +  profile;
                }
            }
        };
    })
angular.module("app.rank", [])
    .directive("rank", function () {
        return {
            restrict: "A",

            scope: {
                rank: "="
            },

            controller: function($scope) {

            },

            templateUrl: "/app/directives/rank/rankView.html"
        }
    })
angular.module("app.sidebar", [])
    .directive("sidebar", function () {
        return {
            restrict: "E",

            controller: function ($scope, InfoService) {
                $scope.updateInfo = function () {
                    InfoService.allInfo(function (success) {
                        $scope.info = success.data;
                    }, function (error) {
                        console.log(error);
                    });

                    InfoService.bitsLeaderboard(function (success) {
                        $scope.bitsLeaderboard = success.data;
                    }, function (error) {
                        console.log(error);
                    });

                    InfoService.xpLeaderboard(function (success) {
                        $scope.xpLeaderboard = success.data;
                    }, function (error) {
                        console.log(error);
                    });
                };

                $scope.updateInfo();
            },

            templateUrl: "/app/directives/sidebarDirective/sidebarView.html"
        };
    })
angular.module('app.about', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('about', {
                    url: '/about',
                    templateUrl: 'app/pages/about/aboutView.html',
                    controller: "AboutController"
                });
        }])
    .controller("AboutController", ["$scope", function ($scope) {

    }]);
angular.module("app.badges", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('badges', {
                    url: '/badges',
                    templateUrl: '/app/pages/badges/badgesView.html',
                    controller: "BadgesController"
                });

        }])
    .controller("BadgesController", ["$scope", "$http", "BadgeService", "AuthService", function ($scope, $http, BadgeService, AuthService) {

        $scope.imageNameFromName = function (name) {
            return name.toLowerCase().replace(/\s+/g, "-");
        };

        $scope.userHasBadge = function (badge) {
            if(AuthService.user) {
                for(var badgeKey in AuthService.user.badges) {
                    var currentBadge = AuthService.user.badges[badgeKey];

                    if(badge.id === currentBadge.id) return true;
                }
            }

            return false;
        };

        $scope.badgeForName = function (name) {
            for(var badgeKey in $scope.badges) {
                var badge = $scope.badges[badgeKey];

                if (badge.name === name) return badge;
            }

            return null;
        };

        BadgeService.getAll(function (success) {
            $scope.badges = success.data;
        }, angular.noop);

        $scope.badgePercentCalculators = {
            "Authentic" : function () {
                return AuthService.user.role == "PENDING" ? 0 : 100;
            },

            "Making Links" : function () {
                return Math.min(AuthService.user.connectedAccounts.length > 0 ? 100 : 0, 100);
            },

            "Full Coverage" : function () {
                return Math.min((AuthService.user.connectedAccounts.length / 5.0) * 100, 100);
            },

            "Feed The Pig" : function () {
                return Math.min((AuthService.user.ranking.points / 5000.0) * 100, 100);
            },

            "Penny-Pincher": function () {
                return Math.min((AuthService.user.ranking.points / 25000.0) * 100, 100);
            },

            "Follow Me": function () {
                return Math.min((AuthService.user.referredUsers / 5.0) * 100, 100);
            },

            "Influential": function () {
                return Math.min((AuthService.user.referredUsers / 25.0) * 100, 100);
            },

            "Natural Leader": function () {
                return Math.min((AuthService.user.referredUsers / 50.0) * 100, 100);
            },

            "Ace High": function () {
                return $scope.userHasBadge($scope.badgeForName("Ace High")) ? 100 : 0;
            },

            "Beginner's Luck": function () {
                return AuthService.user.gamesWon > 0 ? 100 : 0;
            },

            "Victorious" : function () {
                return Math.min((AuthService.user.gamesWon / 5.0) * 100, 100);
            },

            "Hotshot" : function () {
                return Math.min((AuthService.user.gamesWon / 10.0) * 100, 100);
            },

            "Steamroller" : function () {
                return Math.min((AuthService.user.gamesWon / 25.0) * 100, 100);
            },

            "Hot Streak": function () {
                return Math.min(AuthService.user.gameStreak / 3.0 * 100, 100);
            },

            "Cake Day": function () {
                return $scope.userHasBadge($scope.badgeForName("Cake Day")) ? 100 : 0;
            }
        }

    }]);
angular.module('app.auth', [
    'ui.router'
])

    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('auth', {
                    url: '/login',
                    templateUrl: 'app/pages/auth/loginView.html',
                    controller: 'LoginController'
                });
        }])


    .controller('LoginController', function ($scope, $rootScope, AuthService, $http) {
    });
angular.module("app.blog", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('blog', {
                    url: '/blog',
                    templateUrl: '/app/pages/blog/blogView.html',
                    controller: "BlogController"
                });

        }])
    .controller("BlogController", ["$scope", "BlogService", "$mdDialog", "ToastService", "AuthService", "$anchorScroll", function ($scope, BlogService, $mdDialog, ToastService, AuthService, $anchorScroll) {
        $scope.posts = [];

        $scope.AuthService = AuthService;

        $scope.updatePosts = function () {
            BlogService.allPosts(function (success) {
                console.log(success.data);
                $scope.posts = success.data;
            }, function (error) {
                console.log(error);
            });
        }

        $scope.newPost = function ($event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/addBlogPostDialog/addBlogPostDialogView.html",
                controller: "AddBlogPostDialogController",
                targetEvent: $event
            })
                .then(function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Successfully published", success.title);
                    $scope.updatePosts();
                }, function (error) {
                    //Otherwise means they just clicked cancel
                    if(error) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not publish post");
                    }
                })
        };

        $scope.editPost = function (post, $event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/addBlogPostDialog/addBlogPostDialogView.html",
                controller: "EditBlogPostDialogController",
                targetEvent: $event,

                locals: {
                    post: post
                }
            })
                .then(function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Edited post");
                    $scope.updatePosts();
                }, angular.noop)
        }

        $scope.deletePost = function (post) {
            BlogService.deleteBlog(post.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Successfully deleted post", success.data.title);
                $scope.updatePosts();
            }, function () {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not delete post");
            })
        }

        $scope.updatePosts();
    }])
var home = angular.module('app.coming', [
    'ui.router'
]);

home.config(['$stateProvider',
    function ($stateProvider) {

        $stateProvider
            .state('coming', {
                url: '/coming',
                templateUrl: '/app/pages/coming/comingView.html'
            });

    }]);
angular.module("app.dashboard", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('dashboard', {
                    name: "dashboard",
                    url: "/dashboard",
                    abstract: true,

                    templateUrl: '/app/pages/dashboard/dashboardView.html',
                    controller: "DashboardController"
                })
                .state('dashboard.home', {
                    url: '',

                    parent: "dashboard",

                    views: {
                        '' : {
                            templateUrl: '/app/pages/dashboard/dashboardView.html',
                            controller: "DashboardController"
                        },

                        'dashboardInner@dashboard' : {
                            templateUrl: '/app/pages/dashboard/dashboardHome.html',
                            controller: "DashboardController"
                        }
                    }
                })
                .state('dashboard.profile', {
                    url: '/profile',

                    parent: "dashboard",

                    views: {
                        '' : {
                            templateUrl: '/app/pages/dashboard/dashboardView.html',
                            controller: "DashboardController"
                        },

                        'dashboardInner@dashboard' : {
                            templateUrl: '/app/pages/settings/settingsProfileView.html',
                            controller: "SettingsController"
                        }
                    }
                })
                .state('dashboard.badges', {
                    url: '/badges',

                    parent: "dashboard",

                    views: {
                        '' : {
                            templateUrl: '/app/pages/dashboard/dashboardView.html',
                            controller: "DashboardController"
                        },

                        'dashboardInner@dashboard' : {
                            templateUrl: "/app/pages/badges/badgesView.html",
                            controller: "BadgesController"
                        }
                    }
                });
        }])
    .controller("DashboardController", ["$scope", "AuthService", "UserService", function ($scope, AuthService, UserService) {
        $scope.AuthService = AuthService;

        $scope.onLoad = function () {
            UserService.getActivities(function (success) {
                AuthService.user.activityLog = success.data;
            }, angular.noop);
        };

        if(!AuthService.user) {
            AuthService.callbacks.push($scope.onLoad);
        } else $scope.onLoad();

        $scope.imageForRank = function (rank) {
            var mapping = {
                'Bronze I': "bronze1",
                'Bronze II': "bronze2",
                'Bronze III': "bronze3",
                'Bronze IV': "bronze4",
                'Bronze V': "bronze5",
                'Silver I':  "silver1",
                'Silver II': "silver2",
                'Silver III': "silver3",
                'Silver IV': "silver4",
                'Silver V': "silver5",
                'Gold I': "gold1",
                'Gold II': "gold2",
                'Gold III': "gold3",
                'Gold IV': "gold4",
                'Gold V': "gold5",
                'Platinum I': "platinum1",
                'Platinum II': "platinum2",
                'Platinum III': "platinum3",
                'Platinum IV': "platinum4",
                'Platinum V': "platinum5",
                'Diamond I': "diamond1",
                'Diamond II': "diamond2",
                'Diamond III': "diamond3",
                'Diamond IV': "diamond4",
                'Diamond V': "diamond5",
                'Elite': "elite"
            }

            return mapping[rank];
        }
    }]);
/**
 * Created by Terence on 4/22/2015.
 */
angular.module("app.contact", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('contact', {
                    url: '/contact',
                    templateUrl: '/app/pages/contact/contactView.html',
                    controller: "ContactController"
                });

        }])
    .controller("ContactController", ["$scope", "ToastService", "ContactService", "AuthService", function ($scope, ToastService, ContactService, AuthService) {

        $scope.form = {};
        $scope.AuthService = AuthService;

        $scope.setDefaults = function () {
            if(AuthService.user) {
                $scope.form.name = AuthService.user.username;
                $scope.form.email = AuthService.user.email;
            } else
            {
                AuthService.callbacks.push($scope.setDefaults);
            }
        };

        $scope.submit = function (form) {
            if(form.name &&
                form.email &&
                form.type &&
                form.text) {

                if(form.text.length > 1000) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Enquiry too long");
                } else {
                    ContactService.create(form.name, form.text, form.type, form.email, function (success) {
                        ToastService.showDevwarsToast("fa-check-circle", "Success", "We have received your feedback");

                        delete $scope.form;
                    }, function (error) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Something went wrong");
                    })
                }
            } else {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing fields");
            }
        };

        $scope.setDefaults();

    }]);
/**
 * Created by Terence on 3/28/2015.
 */
angular.module("app.gameControlPanel", [])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('gameControlPanel', {
                    url: '/gpanel',
                    templateUrl: '/app/pages/gameControlPanel/gameControlPanelView.html',
                    controller: "GameControlPanelController"
                });

        }])
    .controller("GameControlPanelController", ["$scope", "GameService", "$location", "AuthService", "$rootScope", "$mdDialog", "ToastService", "$interval", "DialogService", function ($scope, GameService, $location, AuthService, $rootScope, $mdDialog, ToastService, $interval, DialogService) {
        var $routeParams = $location.search();

        $scope.game = null;
        $scope.pickedDate = new Date();
        $scope.pickedTime = new Date();

        $scope.updateGame = function () {
            if ($routeParams.game) {
                GameService.getGame($routeParams.game, function (success) {
                    $scope.game = (success.data);

                    $scope.pickedDate = new Date($scope.game.timestamp);
                    $scope.pickedTime = new Date($scope.game.timestamp);

                    $scope.updateSignedUpUsers();
                }, function (error) {
                    console.log(error);
                });
            } else {
                GameService.currentGame(function (success) {
                    $scope.game = success.data;

                    $scope.pickedDate = new Date($scope.game.timestamp);
                    $scope.pickedTime = new Date($scope.game.timestamp);

                    $scope.updateSignedUpUsers();
                }, function (error) {
                    console.log(error);

                    GameService.nearestGame(function (nearestSuccess) {
                        $scope.game = nearestSuccess.data;

                        $scope.pickedDate = new Date($scope.game.timestamp);
                        $scope.pickedTime = new Date($scope.game.timestamp);

                        $scope.updateSignedUpUsers();
                    }, function (nearestError) {
                        console.log(nearestError);
                    });
                });
            }
        };

        $scope.updateSignedUpUsers = function () {
            GameService.pendingPlayers($scope.game.id, function (success) {
                console.log(success.data);
                $scope.signedUpUsers = success.data;
            }, function (error) {

            })
        }

        $scope.saveGame = function () {

            $scope.getGameTimestamp();

            //Remove all completed objectives is game has been removed
            for(var teamKey in $scope.game.teams) {
                var team = $scope.game.teams[teamKey];

                for(var completedObjectiveKey in team.completedObjectives) {
                    var completedObjective = team.completedObjectives[completedObjectiveKey].objective;

                    var foundObjective = false;
                    for(var gameObjectiveKey in $scope.game.objectives) {
                        var gameObjective = $scope.game.objectives[gameObjectiveKey];

                        if(gameObjective.id && completedObjective.id && gameObjective.id === completedObjective.id) {
                            foundObjective = true;
                        }
                    }

                    if(!foundObjective) {
                        team.completedObjectives.splice(completedObjectiveKey, 1);
                    }
                }
            }

            GameService.editGame($scope.game.id, JSON.stringify($scope.game), function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Saved Game");
                $scope.updateGame();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not save game.")
            })
        }

        $scope.addPlayer = function (game, $event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/addPlayerDialog/addPlayerDialogView.html",
                controller: "AddPlayerDialogController",
                targetEvent: $event,

                locals: {
                    game: game
                }
            })
                .then(function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Successfully added player", success.newPlayer.user.username);
                    $scope.updateGame();
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not add player.");
                });
        }

        $scope.removePlayer = function (game, player, $event) {
            GameService.removePlayer(game.id, player.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Successfully removed player", player.user.username);
                $scope.updateGame();
            }, function (error) {
                console.log(error);
            })

            $event.stopPropagation();
        };

        $scope.editPlayer = function (game, player, team, $event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/editPlayerDialog/editPlayerDialogView.html",
                controller: "EditPlayerDialogController",
                targetEvent: $event,

                locals: {
                    game: game,
                    player: player,
                    team: team
                }
            })
                .then(function (success) {
                    $scope.updateGame();
                    ToastService.showDevwarsToast("fa-check-circle", "Successfully edited player", success.oldPlayer.user.username);
                }, function (error) {

                })
        };

        $scope.getGameTimestamp = function () {
            if($scope.pickedDate) {
                $scope.pickedDate.setHours(0);
                $scope.pickedDate.setMinutes(0);
                $scope.pickedDate.setSeconds(0);
                $scope.pickedDate.setMilliseconds(0);

                $scope.game.timestamp = $scope.pickedDate.getTime();

                if($scope.pickedTime) {
                    $scope.game.timestamp += ((($scope.pickedTime.getHours() * 60) + $scope.pickedTime.getMinutes()) * 60 * 1000);
                }
            }

            return $scope.game.timestamp;
        };

        $scope.activateGame = function () {
            GameService.activateGame($scope.game.id, function (success) {
                $scope.game = success.data;
            }, angular.noop);
        };

        $scope.deactivateGame = function () {
            GameService.deactivateGame($scope.game.id, function (success) {
                $scope.game = success.data;
            }, angular.noop);
        };

        $scope.addPointsToTeamInGame = function (team, game, $event) {
            DialogService.getInputWithMessage("Add points to " + team.name, "Points", $event, function (success) {
                GameService.addPointsToTeam(team.id, game.id, 0, success, function (res) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Added " + success + " points to " + team.name);
                }, angular.noop);
            });
        };

        $scope.addXPToTeamInGame = function (team, game, $event) {
            DialogService.getInputWithMessage("Add XP to " + team.name, "XP", $event, function (success) {
                GameService.addPointsToTeam(team.id, game.id, success, 0, function (res) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Added " + success + " XP to " + team.name);
                }, angular.noop);
            });
        }

        $scope.toggleTeamObjective = function (team, objective) {
            if(!team.completedObjectives) {
                console.log("Resetting");
                team.completedObjectives = [];
            }

            if($scope.teamHasObjective(team, objective)) {
                var index = -1;

                for(var i = 0; i < team.completedObjectives.length; i++) {
                    if(objective.id === team.completedObjectives[i].objective.id) {
                        index = i;
                        i = team.completedObjectives.length;
                    }
                }

                if(index > -1) {
                    team.completedObjectives.splice(index, 1);
                    console.log("Able to remove");
                }
            } else {
                team.completedObjectives.push({
                    team_id: team.id,
                    objective: objective
                });

                console.log("Able to add");
            }

            /*console.log("Does the team have the objective now?");
             console.log($scope.teamHasObjective(team, objective));
             console.log(team.completedObjectives);*/
        };

        $scope.teamHasObjective = function (team, objective) {
            for(var objectiveKey in team.completedObjectives) {
                var teamObjective = team.completedObjectives[objectiveKey].objective;

                if(teamObjective.id === objective.id) {
                    return true;
                }
            }
            //console.log("Returning false for " +  team.name + " : " + objective.objectiveText);
            return false;
        }

        $scope.removeObjectiveFromGame = function (objective, game) {
            if(game.objectives) {
                game.objectives.splice(game.objectives.indexOf(objective), 1);
            }

            for(var teamKey in game.teams) {
                var team = game.teams[teamKey];

                for(var completedObjectiveKey in team.completedObjectives) {
                    var completedObjective = team.completedObjectives[completedObjectiveKey].objective;

                    if(completedObjective.id === objective.id) {
                        team.completedObjectives.splice(team.completedObjectives.indexOf(completedObjective, 1));
                    }
                }
            }
        }

        $scope.endGame = function (game) {
            GameService.endGame(game.id, game.teams.red.win ? game.teams.red.id : game.teams.blue.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Ended Game");
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not end game");
            })
        }

        $rootScope.$on("$locationChangeStart", function (event, newState, oldState) {
            var newStateParams = newState.split("?")[1];
            var oldStateParams = oldState.split("?")[1];

            var newRootURL = newState.split("?")[0];
            var oldRootURL = oldState.split("?")[0];

            if (newRootURL === oldRootURL && newStateParams && !oldStateParams) location.reload();

            if (newRootURL === oldRootURL && oldStateParams && !newStateParams) location.reload();

            if (newRootURL === oldRootURL && newStateParams && oldStateParams && newStateParams !== oldStateParams) location.reload()
        });

        $scope.updateGame();
    }]);
angular.module("app.games", [])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('games', {
                    url: '/games',
                    templateUrl: '/app/pages/games/gamesView.html',
                    controller: "GameController"
                });
        }])
    .controller("GameController", ["$scope", "GameService", "AuthService", "$mdDialog", "$mdToast", "$filter", "ToastService", "DialogService", function ($scope, GameService, AuthService, $mdDialog, $mdToast, $filter, ToastService, DialogService) {
        $scope.games = [];
        $scope.AuthService = AuthService;

        $scope.options = {
            thickness: 200,
            legend: false
        };

        $scope.labels = [
            'blue',
            'red'
        ]

        $scope.DialogService = DialogService;

        GameService.pastGames(function (success) {
            $scope.pastGames = success.data;

            for(var key in $scope.pastGames) {
                var game = $scope.pastGames[key];

                for(var teamKey in game.teams) {
                    var team = game.teams[teamKey];

                    var players = team.players;
                    var sortedPlayers = [];

                    for(var playerKey in players) {
                        var player = players[playerKey];

                        if(player.language.toLowerCase() === "html") sortedPlayers[0] = player;
                        if(player.language.toLowerCase() === "css") sortedPlayers[1] = player;
                        if(player.language.toLowerCase() === "js") sortedPlayers[2] = player;
                    }

                    console.log(sortedPlayers);

                    team.players = sortedPlayers;
                }
            }

            if($(document).width() > 840)
                $scope.setSelectedGame($scope.pastGames[0]);
        }, angular.noop);

        GameService.upcomingGames(function (success) {
            $scope.upcomingGames = success.data;
        }, angular.noop);

        $scope.signupForGame = function (game, $event) {
            if (AuthService.user && AuthService.user.role !== "PENDING") {
                DialogService.applyForGame(game, $event);
            } else {
                if(!AuthService.user) {
                    DialogService.signup($event);
                } else {
                    ToastService.showDevwarsErrorToast("fa-envelope-o", "Error", "Please confirm your email before applying for games.")
                }
            }
        };

        $scope.setSelectedGame = function (game, $index) {
            $scope.selectedGame = game;

            if($index > -1 && (new Date().getTime() - $scope.lastTimeClicked) > 200) {
                if($(".game" + $index).next().hasClass("slide")) {
                    $(".game" + $index).next().slideToggle();
                    $(".game" + $index).next().removeClass("slide");
                } else {
                    $(".slide").slideToggle();
                    $(".slide").removeClass("slide");

                    $(".game" + $index).next().slideToggle();
                    $(".game" + $index).next().addClass("slide");
                }

                $scope.lastTimeClicked = new Date().getTime();
            }

            $scope.designData = $scope.dataForGameCategory($scope.selectedGame, 'design');
            $scope.funcData = $scope.dataForGameCategory($scope.selectedGame, 'func');
            $scope.codeData = $scope.dataForGameCategory($scope.selectedGame, 'code');
        }

        $scope.isGameSelected = function (game) {
            return game.id === $scope.selectedGame.id;
        }

        $scope.moderateGame = function (game) {
            window.location = "/#/gpanel?game=" + game.id;
        }

        $scope.dataForGameCategory = function (game, category) {
            var data = [];

            for(var teamKey in game.teams)
            {
                var team = game.teams[teamKey];

                if(team[category + "Votes"]) {
                    data.push({
                        label: team.name,
                        value: team[category + "Votes"],
                        color: team.name === "blue" ? "#03A9F4" : "#E91E63"
                    });
                }
            }

            return data;
        };

        $scope.getVotePointsEarned = function (teamName, game) {
            var otherTeam = null;
            var team = null;

            if(teamName === "red") {
                team = game.teams['red'];
                otherTeam = game.teams['blue'];
            } else if(teamName === "blue") {
                team = game.teams['blue'];
                otherTeam = game.teams['red'];
            }

            var total = 0;

            if(team.designVotes >= otherTeam.designVotes && (team.designVotes !== 0 && otherTeam.designVotes !== 0)) total+=2;
            if(team.funcVotes >= otherTeam.funcVotes  && (team.funcVotes !== 0 && otherTeam.funcVotes !== 0)) total+=2;
            if(team.codeVotes >= otherTeam.codeVotes  && (team.codeVotes !== 0 && otherTeam.codeVotes !== 0)) total+=2;

            //Last objective is two so add one if they aced
            if(team.completedObjectives.length === game.objectives.length) {
                total++;
            }

            return total;
        };

        $scope.getOtherTeam = function (team, game) {
            return game.teams[team.name === "red" ? "blue" : "red"];
        };

        $scope.getAllGames = function (success) {
            GameService.allGames(0, 5, function (allGamesSuccess) {
                var returnGames = allGamesSuccess.data;

                $scope.games = $scope.games.concat(returnGames);

                for (var i = 0; i < $scope.games.length; i++) {
                    var game = $scope.games[i];

                    if (success && game.id === success.data.id) {
                        $scope.games.splice(i, 1);
                        i = $scope.games.length;
                    }
                }

                console.log($scope.games);
            }, function (allGamesError) {
                console.log(allGamesError);
            });
        };

        $scope.teamHasObjective = function (team, objective) {
            for(var objectiveKey in team.completedObjectives) {
                var teamObjective = team.completedObjectives[objectiveKey].objective;

                if(teamObjective.id === objective.id) {
                    return true;
                }
            }
            return false;
        };

        $scope.sortPlayers = function (data) {
            var langs = ["html", "css", "js"];

            data.sort(function (a) {
                return langs.indexOf(a.language.toLowerCase());
            });
        };

        GameService.nearestGame(function (success) {
            $scope.games.push(success.data);

            $scope.getAllGames(success);
        }, function (error) {
            console.log(error);
            $scope.getAllGames();
        });

        $scope.lastTimeClicked = new Date().getTime();
    }]);
/**
 * Created by Beau on 01 May 2015.
 */
angular.module("app.help", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('help', {
                    url: '/help',
                    templateUrl: '/app/pages/help/helpView.html',
                    controller: "HelpController"
                });

        }])
    .controller("HelpController", ["$scope", function ($scope) {

    }]);

app.controller('HelpController', function($scope, $location, $anchorScroll) {
    $scope.scrollTo = function(id) {
        $location.hash(id);
        $anchorScroll();
    }
});

/**
 * Created by Terence on 3/23/2015.
 */
var home = angular.module('app.home', [
    'ui.router'
]);

home.config(['$stateProvider',
    function ($stateProvider) {

        $stateProvider
            .state('home', {
                url: '/',
                templateUrl: '/app/pages/home/homeView.html',
                controller: "HomeController"
            });

    }]);

home.controller("HomeController", ["$scope", "InfoService", "DialogService", "BlogService", "$location", "$anchorScroll", "AuthService", function ($scope, InfoService, DialogService, BlogService, $location, $anchorScroll, AuthService) {

    $scope.DialogService = DialogService;
    $scope.AuthService = AuthService;

    $scope.updateInfo = function () {
        InfoService.allInfo(function (success) {
            $scope.info = success.data;
        }, function (error) {
            console.log(error);
        })

        InfoService.bitsLeaderboard(function (success) {
            $scope.bitsLeaderboard = success.data;
        }, function (error) {
            console.log(error);
        })

        InfoService.xpLeaderboard(function (success) {
            $scope.xpLeaderboard = success.data;
        }, function (error) {
            console.log(error);
        })

        BlogService.allPosts(function (success) {
            success.data.splice(2, success.data.length - 2);
            $scope.posts = success.data;

            console.log($scope.posts);
        }, function (error) {
            console.log(error);
        })
    };

    $scope.scrollToBlogPost = function (id) {
        location.href = "/blog#" + id;
    };

    $scope.updateInfo();
}]);
angular.module('app.leaderboards', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('leaderboards', {
                    url: '/leaderboards',
                    templateUrl: 'app/pages/leaderboards/leaderboardsView.html',
                    controller: "LeaderboardsController"
                });
        }])
    .controller("LeaderboardsController", ["$scope", "InfoService", function ($scope, InfoService) {

        $scope.page = 0;
        $scope.users = [];

        $scope.loadMore = function () {
            InfoService.leaderboard($scope.page, function (success) {
                for(var key in success.data) {
                    var user = success.data[key][0];
                    user.score = success.data[key][1];
                    $scope.users.push(user);
                }

                $scope.page++;
            }, function (error) {
                console.log(error);
            });
        };

        $scope.loadMore();

    }]);
/**
 * Created by Terence on 5/6/2015.
 */
angular.module("app.liveGame", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('livegame', {
                    url: '/live',
                    templateUrl: '/app/pages/livegame/livegameView.html',
                    controller: "LiveGameController"
                });

        }])
    .controller("LiveGameController", ["$scope", "GameService", function ($scope, GameService) {

        $scope.live = false;

        GameService.currentGame(function (success) {
            $scope.live = true;
        }, function (error) {
            $scope.live = false; //Redundancy
        })

    }]);
/**
 * Created by Beau on 16 May 2015.
 */
angular.module("app.profile", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('profile', {
                    url: '/profile',
                    templateUrl: '/app/pages/profile/profileView.html',
                    controller: "ProfileController"
                });

        }])
    .controller("ProfileController", ["$scope", "AuthService", "UserService", "$location", function ($scope, AuthService, UserService, $location) {

        $scope.AuthService = AuthService;

        var routeParams = $location.search();

        if(routeParams.username) {
            UserService.getPublicUser(routeParams.username, function (success) {
                $scope.user = success.data;
            }, function (error) {
                console.log(error);
            })
        }
    }]);
angular.module('app.modCP', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('modCP', {
                    url: '/modCP',
                    abstract: true,
                    name: "modCP",

                    templateUrl: 'app/pages/modCP/modCPView.html',
                    controller: "ModCPController"
                })

                .state('modCP.createGameView', {
                    url: '/creategame',
                    parent: "modCP",

                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPcreateGameView.html",
                            controller: "ModCPController"
                        }
                    }
                })

                .state('modCP.createTeamsView', {
                    url: '/createteams',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPcreateTeamsView.html",
                            controller: "ModCPController"
                        }
                    }
                })

                .state('modCP.createObjectiveView', {
                    url: '/createobjectives',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPcreateObjectivesView.html",
                            controller: "ModCPController"
                        }
                    }
                })

                .state('modCP.liveGameView', {
                    url: '/livegame',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPliveGameView.html",
                            controller: "ModCPController"
                        }
                    }
                })

                .state('modCP.PostGameView', {
                    url: '/postgame',
                    parent: "modCP",

                    views : {
                        '' : {
                            templateUrl: 'app/pages/modCP/modCPView.html',
                            controller: "ModCPController"
                        },

                        'modCPInner@modCP': {
                            templateUrl: "app/pages/modCP/modCPPostGameView.html",
                            controller: "ModCPController"
                        }
                    }
                })
            ;
        }])

    .controller("ModCPController", ["$scope", "GameService", "ToastService", "$filter", "$mdDialog", "$location", function($scope, GameService, ToastService, $filter, $mdDialog, $location){

        $scope.pickedDate = new Date();
        $scope.pickedTime = new Date();

        $scope.availableGames = null;

        $scope.dateWatch = function () {
            $scope.gameDate = new Date($scope.pickedDate.getTime());

            $scope.gameDate.setHours($scope.pickedTime.getHours());
            $scope.gameDate.setMinutes($scope.pickedTime.getMinutes());
            $scope.gameDate.setSeconds(0);
            $scope.gameDate.setMilliseconds(0);
        };

        $scope.updateGames = function () {
            GameService.allGames(0, 10, function (success) {
                $scope.availableGames = success.data;

                console.log($location.search());
                if($location.search().game) {
                    var game = parseInt($location.search().game);

                    $scope.availableGames.forEach(function (a) {
                        if(a.id === game) $scope.selectedGame = a;
                    });
                };
            }, angular.noop);
        };

        $scope.gameLabel = function (game) {
            return $filter('date')(game.timestamp, 'mediumDate') + " - " + game.name;
        }

        $scope.createGame = function () {

            $scope.dateWatch();

            GameService.createGame($scope.name, $scope.gameDate.getTime(), function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Created Game");
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not create game");
            })

        };

        $scope.addPlayer = function (player) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/addSelectedPlayerDialog/addSelectedPlayerDialogView.html",
                controller: "AddSelectedPlayerDialogController",

                locals : {
                    player: player,
                    game: $scope.selectedGame
                }
            })
                .then(function (success) {
                    GameService.addPlayer(success.team.id, $scope.selectedGame.id, success.language, JSON.stringify(player), function (success) {
                        $scope.updateSelectedGame();
                    }, function (error) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not add player");
                    });
                }, angular.noop);
        };

        $scope.updateSelectedGame = function () {
            GameService.getGame($scope.selectedGame.id, function (success) {
                $scope.selectedGame = success.data;
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not refresh game");
            })
        };

        $scope.removePlayer = function (player) {
            GameService.removePlayer($scope.selectedGame.id, player.id, function (success) {
                $scope.updateSelectedGame();
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Removed " + player.user.username);
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamtion-circle", "Error", "Could not remove player");
            });
        };

        $scope.forfeitPlayer = function (player) {
            GameService.forfeitUser(JSON.stringify(player), function (success) {

                $scope.updateSelectedGame();

                ToastService.showDevwarsToast("fa-check-circle", "Success", "Forfeited Player");
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not forfeit player");
            });
        };

        $scope.toggleObjective = function (objective, team) {

            var hasObjective = $scope.teamHasObjective(team, objective);

            console.log("Has Objective : " + hasObjective);

            if(hasObjective) {
                team.completedObjectives = team.completedObjectives.filter(function (a) {
                    return a.objective.id !== objective.id;
                })
            } else {
                team.completedObjectives.push({
                    objective : objective,
                    team_id: team.id
                });
            }

            console.log(team.completedObjectives);
        };

        $scope.teamHasObjective = function(team, objective) {
            var hasObjective = false;

            team.completedObjectives.forEach(function (a) {
                if(a.objective.id == objective.id) hasObjective = true;
            });

            return hasObjective;
        };

        $scope.activateGame = function (game) {
            GameService.activateGame(game.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Activated Game");

                $scope.updateSelectedGame();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle",  "Error", "Could not activate game");
            })
        };

        $scope.deactivateGame = function (game) {
            GameService.deactivateGame(game.id, function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Deactivated Game");

                $scope.updateSelectedGame();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle",  "Error", "Could not deactivate game");
            })
        };

        $scope.saveGame = function (game) {

            //Remove objectives with no text (They're empty)
            if(game.objectives) {
                game.objectives = game.objectives.filter(function (objective) {
                    return objective.objectiveText.length > 0;
                });
            }

            for(var teamKey in game.teams) {
                var team = game.teams[teamKey];

                //Cascade to team's completed objectives
                if(team.completedObjectives) {
                    team.completedObjectives = team.completedObjectives.filter(function (completed) {
                        var found = false;

                        if(game.objectives) {
                            game.objectives.forEach(function (objective) {
                                if (objective.id == completed.objective.id) found = true;
                            });
                        }

                        return found;
                    });
                }
            }

            //Init their order ids since they're already in order
            game.objectives.forEach(function (a, index) {
                a.orderID = index;
            });

            GameService.editGame(game.id, JSON.stringify(game), function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Saved game");

                $scope.selectedGame = success.data;
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not save game");
            })
        };

        $scope.endGame = function (game, winner) {
            GameService.endGame(game.id, winner.id, function (success) {
                $scope.updateSelectedGame();

                ToastService.showDevwarsToast("fa-check-circle", "Success", winner.name + " won : Game Over");
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not end game");
            });
        };

        //Watchers
        $scope.$watch('selectedGame', function (oldVal, newVal) {

            GameService.pendingPlayers($scope.selectedGame.id, function (success) {
                $scope.selectedGame.appliedUsers = success.data;
            }, angular.noop);

        });

        $scope.updateGames();
    }]);







angular.module('app.settings', [
    'ui.router'
])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('settings', {
                    url: '/settings',
                    abstract: true,
                    name: "settings",

                    templateUrl: 'app/pages/settings/settingsView.html',
                    controller: "SettingsController"
                })

                .state('settings.accountView', {
                    url: '',
                    parent: "settings",

                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsAccountView.html",
                            controller: "SettingsController"
                        }
                    }
                })

                .state('settings.profileView', {
                    url: '/profile',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsProfileView.html",
                            controller: "SettingsController"
                        }
                    }
                })
                .state('settings.notificationsView', {
                    url: '/notifications',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsNotificationsView.html",
                            controller: "SettingsController"
                        }
                    }
                })
                .state('settings.connectView', {
                    url: '/connections',
                    views : {
                        '' : {
                            templateUrl: 'app/pages/settings/settingsView.html',
                            controller: "SettingsController"
                        },

                        'settingsInner@settings': {
                            templateUrl: "app/pages/settings/settingsConnectView.html",
                            controller: "SettingsController"
                        }
                    }
                });
        }])
    .controller("SettingsController", ["$scope", "AuthService", "$http", "$mdDialog", "ToastService", "UserService", "$location", "DialogService", function ($scope, AuthService, $http, $mdDialog, ToastService, UserService, $location, DialogService) {
        $scope.AuthService = AuthService;

        var routeParams = $location.search();

        $scope.initProfile = function () {
            if(!AuthService.user) {
                $scope.profile = {};
                AuthService.callbacks.push($scope.initProfile);
            } else {
                $scope.profile = AuthService.user;

                console.log($scope.profile);
            }
        };

        $scope.checkVeteran = function () {
            if(!AuthService.user) {
                AuthService.callbacks.push($scope.checkVeteran);
            } else {
                if(routeParams.veteran) {
                    var twitchUsername = null;

                    for(var key in AuthService.user.connectedAccounts) {
                        if(AuthService.user.connectedAccounts[key].provider === 'TWITCH') twitchUsername = AuthService.user.connectedAccounts[key].username;
                    }

                    DialogService.showConfirmDialog("Account Transfer", "We have record that you have played in Season 1 with your Twitch account " + twitchUsername + ". We have that username reserved for you. Would you like to claim it or keep your existing username? If you keep your username, we will release the username " + twitchUsername + " to the public.", "Claim " + twitchUsername, "Keep my username", null)
                        .then(function (success) {
                            UserService.claimTwitch(function (success) {
                                location.href = "/settings/connections"
                            }, function (error) {
                                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                            });
                        }, function (error) {
                            UserService.releaseUsername(function (success) {
                                location.href = "/settings/connections"
                            }, function (error) {
                                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                            });
                        });
                }
            }
        };

        $scope.disconnect = function (provider) {
            if(AuthService.hasProvider(provider)) {
                $http({
                    method: "GET",
                    url: "/v1/connect/" + provider +  "/disconnect"
                })
                    .then(function (success) {
                        console.log(success);
                        location.reload();
                    }, function (error) {
                        console.log(error);
                    });
            } else
            {
                location.href = "/v1/connect/" + provider;
            }
        };

        $scope.editAvatarImage = function (image, $event) {
            $mdDialog.show({
                templateUrl: "/app/components/dialogs/editAvatarImageDialog/editAvatarImageView.html",
                controller: "EditAvatarImageDialogController",
                clickOutsideToClose: false,

                locals: {
                    image: image
                }
            })
                .then(function (success) {
                    location.reload();
                }, function (error) {

                })
        };

        $scope.passwordsMatch = function (passwordChange) {
            return passwordChange.newPassword1 === passwordChange.newPassword2
        }

        $scope.changePassword = function (passwordChange) {
            if(passwordChange.newPassword1
                && passwordChange.newPassword2
                && passwordChange.newPassword1 === passwordChange.newPassword2) {

                UserService.changePassword(passwordChange.newPassword1, passwordChange.currentPassword, function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Changed your password");
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                })
            }
        };

        $scope.changeEmail = function (changeEmail) {
            if(changeEmail.newEmail && changeEmail.currentPassword) {
                UserService.changeEmail(changeEmail.newEmail, changeEmail.currentPassword, function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Changed Email");
                    AuthService.init();
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                })
            }
        };

        $scope.updateInfo = function (profile) {
            UserService.updateInfo(profile.company, profile.location, profile.url, profile.username, function (success) {
                location.reload();
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
            })
        };

        $scope.$watch("selectedAvatarImage", function (oldVal, newVal) {

            if(oldVal !== newVal)
                $scope.editAvatarImage($scope.selectedAvatarImage);
        });

        $scope.initProfile();
    }]);
/**
 * Created by Beau on 16 May 2015.
 */
angular.module("app.shop", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('shop', {
                    url: '/shop',
                    templateUrl: '/app/pages/shop/shopView.html',
                    controller: "ShopController"
                });

        }])
    .controller("ShopController", ["$scope", function ($scope) {

    }]);
/**
 * Created by Beau on 07 May 2015.
 */
angular.module("app.verify", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('verify', {
                    url: '/verify',
                    templateUrl: '/app/pages/verify/verifyView.html',
                    controller: "VerifyController"
                });

        }])
    .controller("VerifyController", ["$scope", function ($scope) {

    }]);

app.controller('VerifyController', ["$scope", "$location", "$anchorScroll", "$interval", function($scope, $location, $anchorScroll, $interval) {
    var routeParams = $location.search();

    $scope.username = routeParams.username;

    $scope.seconds = 10;

    $interval(function () {
        $scope.seconds--;

        if($scope.seconds === 0) {
            location.href = "/#/dashboard";
        }
    }, 1000, 10, true);

    $scope.scrollTo = function(id) {
        $location.hash(id);
        $anchorScroll();
    }
}]);

angular.module("app.warriorReg", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('warriorReg', {
                    url: '/warrior-signup',
                    templateUrl: '/app/pages/warriorReg/warriorRegView.html',
                    controller: "warriorRegController"
                });

        }])
    .controller("warriorRegController", ["$scope", "AuthService", "WarriorService", "ToastService", "$http", function ($scope, AuthService, WarriorService, ToastService, $http) {

        $scope.AuthService = AuthService;

        $scope.warrior = {};

        $scope.warrior.htmlRate = 1;
        $scope.warrior.cssRate = 1;
        $scope.warrior.jsRate = 1;

        $scope.initWarrior = function () {
            if(AuthService.user) {
                console.log(AuthService.user);
                if(AuthService.user.warrior) location.href = "/";
            } else {
                AuthService.callbacks.push($scope.initWarrior);
            }
        };

        $scope.skillNames = ["Novice", "Beginner", "Intermediate", "Advanced", "Expert"];

        $scope.htmlTooltips = ["You have a basic understanding of HTML, and can use essential tags.", "You have a working knowledge of HTML. You understand the basic syntax and methodology of ordering and adding elements to the DOM (Document Object Model).", "You have a strong understanding of HTML, and ability to structure a webpage. You have a knowledge of a variety of attributes, and know how to use them correctly. You have a strong understanding of semantics and structural meaning (parameter usage) .", "You have worked on larger scale projects, and collaborated with other people. You have strong experience working on various development projects. You have worked with SEO tools.", "You use HTML at a production level. You can create websites from scratch with no use of frameworks, and have worked as a front-end developer. You have used Shadow DOM in the past."];
        $scope.cssTooltips = ["You have basic knowledge of CSS. You understand the basic syntax and methodology of cascading stylesheets", "You have a working knowledge of CSS. You have style elements, and know about different frameworks. You have used Bootstrap. You have heard of pre-processors. ", "You have extensive CSS knowledge. You have experience working with responsive design, and understand the mobile-first ideology. You understand the issues that can occur with different browsers. You have used a CMS in the past, and can style elements on them ", "You have worked on larger scale projects, and collaborated with other people. You have strong experience working on various development projects. You understand BEM practices, and implement them ", "You use CSS at a production level. You can style websites from scratch with no use of frameworks, and have worked as a front-end developer"];
        $scope.jsTooltips = ["You have a basic knowledge of JavaScript.", "You have working knowledge of JavaScript. You understand the basic syntax and methodology of OOP", "You have used Javascript with a popular library or framework, and used it for DOM manipulation.", "You have worked on larger scale projects, and collaborated with other people. You have strong experience working on various development projects. You have knowledge of MVC and it's use in creating web application.", "You use JavaScript at a production level. You make full use of MVC and MV Frameworks, have and incredible understaning of OOP, and have worked as a professional front-end developer"];

        $scope.htmlTooltip = $scope.htmlTooltips[0];
        $scope.cssTooltip = $scope.cssTooltips[0];
        $scope.jsTooltip = $scope.jsTooltips[0];

        $scope.getRange = function (start, finish) {
            var returnArray = [];

            for(var i = start; i <= finish; i++) {
                returnArray.push(i);
            }

            return returnArray;
        };

        $scope.setHtml = function (rate) {
            $scope.htmlTooltip = $scope.htmlTooltips[rate - 1];
            $scope.warrior.htmlRate = rate;
        }

        $scope.setCSS = function (rate) {
            $scope.cssTooltip = $scope.cssTooltips[rate - 1];
            $scope.warrior.cssRate = rate;
        }

        $scope.setJS = function (rate) {
            $scope.jsTooltip = $scope.jsTooltips[rate - 1];
            $scope.warrior.jsRate = rate;
        };

        $scope.register = function (warrior) {
            if($scope.hasTS) {
                $http({
                    url: "/v1/warrior/register",
                    method: "GET",
                    params: warrior
                })
                    .then(function (success) {
                        ToastService.showDevwarsToast("fa-check-circle", "Success", "Registered to be a warrior");

                        setTimeout(function () {
                            location.href = "/";
                        }, 2000);
                    }, function (error) {
                        if (error.status === 400) {
                            ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing Parameters");
                        } else {
                            ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                        }
                    });
            } else {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "You must have Teamspeak to be a warrior");
            }
        }

        $scope.initWarrior();

    }]);
angular.module("app.addBlogPostDialog", [])
    .controller("AddBlogPostDialogController", ["$scope", "BlogService", "$mdDialog", "ToastService", function ($scope, BlogService, $mdDialog, ToastService) {
        $scope.done = function () {
            if($scope.title && $scope.description && $scope.text && $scope.image_url) {
                BlogService.createBlog($scope.image_url, $scope.description, $scope.text, $scope.title, $mdDialog.hide, $mdDialog.cancel);
            } else {
                if(!$scope.title && !$scope.description) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing input fields");
                } else {
                    if(!$scope.title) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing title field");
                    } else if(!$scope.description) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing post field");
                    }
                }
            }
        }

        $scope.cancel = function () {
            $mdDialog.cancel();
        }
    }])
    .controller("EditBlogPostDialogController", ["$scope", "post", "$mdDialog", "BlogService", "ToastService", function($scope, post, $mdDialog, BlogService, ToastService) {
        $scope.title = post.title;
        $scope.image_url = post.image_url;
        $scope.description = post.description;
        $scope.text = post.text;

        $scope.done = function () {
            BlogService.updateBlog(post.id, $scope.image_url, $scope.description, $scope.text, $scope.title, function (success) {
                console.log(success);
                $mdDialog.hide();
            }, function (error) {
                console.log(error);
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Couldn't update post");
            })
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        }
    }]);
/**
 * Created by Terence on 4/8/2015.
 */
/**
 * Created by Terence on 4/3/2015.
 */
angular.module("app.addPlayerDialog", [])
    .controller("AddPlayerDialogController", ["$scope", "game", "$mdDialog", "GameService", "ToastService", function ($scope, game, $mdDialog, GameService, ToastService) {

        $scope.game = game;
        $scope.$mdDialog = $mdDialog;

        $scope.pendingPlayers = [];

        GameService.pendingPlayers(game.id, function (success) {
            console.log(success.data);
            $scope.game.pendingPlayers = success.data;
        }, function (error) {
            console.log(error);
        })

        $scope.teamSearchQuery = function () {
            var teamsArray = [];

            for(var lang in game.teams) {
                teamsArray.push(game.teams[lang]);
            }

            return teamsArray.filter(function (team) {
                console.log($scope.selectedTeam);
                return team.name.toLowerCase().indexOf($scope.teamSearchText.toLowerCase()) > -1 || $scope.teamSearchText.toLowerCase().indexOf(team.name.toLowerCase()) > -1;
            });
        };

        $scope.pendingPlayersQuery = function () {
            if($scope.game.pendingPlayers) {
                return $scope.game.pendingPlayers.filter(function (user) {
                    return user.username.toLowerCase().indexOf($scope.searchText.toLowerCase()) > -1 || $scope.searchText.toLowerCase().indexOf(user.username.toLowerCase()) > -1;
                });
            }
        }

        $scope.selectedUserChange = function () {
            $scope.searchText = $scope.selectedUser.username;
        }

        $scope.selectedTeamChange = function () {
            $scope.teamSearchText = $scope.selectedTeam.name;
        }

        $scope.done = function () {
            $scope.conflicts = [];

            //If the moderator didn't click a user but the query still matches an available user
            if(!$scope.selectedUser && $scope.searchText) {
                for(var i = 0; i < $scope.game.pendingPlayers.length; i++) {
                    var pendingUser = $scope.game.pendingPlayers[i];

                    if(pendingUser.username.toUpperCase() === $scope.searchText.toUpperCase()) {
                        $scope.selectedPlayer = {
                            team_id: $scope.selectedTeam.id,
                            language: $scope.selectedLanguage,
                            user: {
                                id: pendingUser.id
                            }
                        };
                    }
                }
            } else if($scope.selectedUser) { //If the moderator did click a player, just make a new player blob to add to the game.
                $scope.selectedPlayer = {
                    team_id : $scope.selectedTeam.id,
                    language: $scope.selectedLanguage,
                    user: {
                        id: $scope.selectedUser.id
                    }
                }
            }

            if($scope.selectedPlayer) { //If all went well, add them to the game on the back end
                GameService.addPlayer($scope.selectedTeam.id, $scope.game.id, $scope.selectedPlayer, function (success) {
                    $mdDialog.hide({
                        newPlayer: success.data
                    });
                }, function (error) {
                    if(error.status === 409) {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Player is already in game.");
                    } else {
                        ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not add player.");
                    }
                });
            } else {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not add player.");
            }

        }

        $scope.cancel = function () {

        }
    }]);
/**
 * Created by Terence on 7/1/2015.
 */
angular.module('app.addSelectedPlayerDialog', [])
    .controller("AddSelectedPlayerDialogController", ['$scope', 'player', 'game', '$mdDialog', function ($scope, player, game, $mdDialog) {

        $scope.$mdDialog = $mdDialog;

        $scope.game = game;

    }]);
/**
 * Created by Terence on 4/5/2015.
 */
angular.module("app.gameSignupConfirm", [])
    .controller("GameSignupConfirmDialogController", ["$scope", "$mdDialog", "game", function ($scope, $mdDialog, game) {
        $scope.game = game;
        $scope.$mdDialog = $mdDialog;

        $scope.confirmSignUp = function () {
            $mdDialog.hide({
                game: game
            });
        };

        $scope.cancelSignUp = function () {
            $mdDialog.cancel();
        };
    }]);
angular.module("app.editAvatarImage", [])
    .controller("EditAvatarImageDialogController", ["$scope", "image", "$http", "$mdDialog", "ToastService", function ($scope, image, $http, $mdDialog, ToastService) {
        $scope.image = image;
        $scope.myCroppedImage = '';

        $scope.testImage = new Image;
        $scope.testImage.src = image;

        $scope.$mdDialog = $mdDialog;

        $scope.minSize = Math.min($scope.testImage.width, $scope.testImage.height);

        setTimeout(function () {
            $scope.minSize = 50;
        }, 500);

        $scope.uploadImage = function (image) {
            var fd = new FormData();
            fd.append("file", dataURItoBlob(image));

            $http.post("/v1/user/changeavatar", fd, {
                withCredentials: true,
                headers: {'Content-Type': undefined},
                transformRequest: angular.identity
            })
                .then(function (success) {
                    $mdDialog.hide();
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                });
        };

        var dataURItoBlob = function(dataURI) {
            var binary = atob(dataURI.split(',')[1]);
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
            var array = [];
            for(var i = 0; i < binary.length; i++) {
                array.push(binary.charCodeAt(i));
            }
            return new Blob([new Uint8Array(array)], {type: mimeString});
        };

    }]);
angular.module("app.confirmDialog", [])
    .controller("ConfirmDialogController", ["$scope", "$mdDialog", "title", "message", "yes", "no", function ($scope, $mdDialog, title, message, yes, no) {
        $scope.$mdDialog = $mdDialog;

        $scope.title = title;
        $scope.message = message;
        $scope.yes = yes;
        $scope.no = no;
    }]);
/**
 * Created by Terence on 4/3/2015.
 */
angular.module("app.editPlayerDialog", [])
    .controller("EditPlayerDialogController", ["$scope", "game", "player", "team", "$mdDialog", "GameService", "ToastService", function ($scope, game, player, team, $mdDialog, GameService, ToastService) {

        $scope.game = game;
        $scope.player = player;

        $scope.selectedUser = player.user;
        $scope.selectedLanguage = player.language;
        $scope.selectedTeam = team;

        $scope.searchText = player.user.username;
        $scope.teamSearchText = team.name;

        $scope.pendingPlayers = [];

        $scope.$mdDialog = $mdDialog;

        GameService.pendingPlayers(game.id, function (success) {
            console.log(success.data);
            $scope.game.pendingPlayers = success.data;
        }, function (error) {
            console.log(error);
        })

        $scope.teamSearchQuery = function () {
            var teamsArray = [];

            for(var lang in game.teams) {
                teamsArray.push(game.teams[lang]);
            }

            return teamsArray.filter(function (team) {
                console.log($scope.selectedTeam);
                return team.name.toLowerCase().indexOf($scope.teamSearchText.toLowerCase()) > -1 || $scope.teamSearchText.toLowerCase().indexOf(team.name.toLowerCase()) > -1;
            });
        };

        $scope.pendingPlayersQuery = function () {
            if($scope.game.pendingPlayers) {
                return $scope.game.pendingPlayers.filter(function (user) {
                    return user.username.toLowerCase().indexOf($scope.searchText.toLowerCase()) > -1 || $scope.searchText.toLowerCase().indexOf(user.username.toLowerCase()) > -1;
                });
            }
        }

        $scope.selectedUserChange = function () {
            $scope.searchText = $scope.selectedUser.username;
        }

        $scope.selectedTeamChange = function () {
            console.log("Team Change");
            $scope.teamSearchText = $scope.selectedTeam.name;
        }

        $scope.done = function () {
            $scope.conflicts = [];

            if($scope.selectedUser === player.user && $scope.searchText) {
                for(var i = 0; i < $scope.game.pendingPlayers.length; i++) {
                    var pendingUser = $scope.game.pendingPlayers[i];

                    if(pendingUser.username.toUpperCase() === $scope.searchText.toUpperCase()) {
                        $scope.selectedPlayer = {
                            team_id: $scope.selectedTeam.id,
                            language: $scope.selectedLanguage,
                            user: {
                                id: pendingUser.id
                            }
                        };
                    }
                }
            } else if($scope.selectedUser !== player.user) {
                $scope.selectedPlayer = {
                    team_id : $scope.selectedTeam.id,
                    language: $scope.selectedLanguage,
                    user: {
                        id: $scope.selectedUser.id
                    }
                }
            }

            if($scope.selectedPlayer && $scope.selectedPlayer.user !== player.user) {
                GameService.editPlayer($scope.selectedTeam.id, $scope.game.id, player.id, $scope.selectedPlayer, function (success) {
                    $mdDialog.hide({
                        oldPlayer: player,
                        newPlayer: success.data
                    });
                }, angular.noop);
            } else {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Could not edit player.");
            }

        }

        $scope.cancel = function () {
            $mdDialog.cancel();
        }
    }]);
/**
 * Created by Terence on 3/22/2015.
 */
angular.module("app")
    .controller("LoginDialogController", ["$scope", "AuthService", "$mdDialog", function ($scope, AuthService, $mdDialog) {

        $scope.$mdDialog = $mdDialog;

        $scope.login = function (credentials) {
            $scope.incorrectCredentials = false;

            AuthService.login(credentials,
                function (success) {
                    console.log(success);
                },
                function (error) {
                    if(error.status === 401) {
                        $scope.incorrectCredentials = true;
                    }
                    console.log(error);
                }
            );
        }

        $scope.oauthLogin = function (provider) {
            console.log(provider);
            window.location = "/v1/oauth/" + provider;
        }
    }]);
/**
 * Created by Terence on 5/4/2015.
 */
angular.module("app.InputDialog", [])
    .controller("InputDialogController", ["$scope", "$mdDialog", "title", "message", function ($scope, $mdDialog, title, message) {
        $scope.$mdDialog = $mdDialog;

        $scope.title = title;
        $scope.message = message;
    }]);
/**
 * Created by Terence on 3/26/2015.
 */
angular.module("app.signup", [])
    .controller("SignupDialogController", ["$scope", "AuthService", "vcRecaptchaService", "ToastService", "$location", "$mdDialog", function ($scope, AuthService, vcRecaptchaService, ToastService, $location, $mdDialog) {
        $scope.conflicts = [];
        $scope.credentials = {};

        $scope.$mdDialog = $mdDialog;

        $scope.setWidgetId = function (widgetId) {
            $scope.widgetId = widgetId;
        }

        $scope.setResponse = function (response) {
            console.log(response);

            $scope.credentials.captchaResponse = response;
        }

        $scope.signup = function (credentials) {
            $scope.conflicts = [];
            $scope.successMessages = [];

            var emailValid = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i.test(credentials.email);
            var usernameValid = /^([A-Za-z0-9\-_]+)$/.test(credentials.username);
            var passwordsMatch = credentials.password && credentials.password2 && credentials.password === credentials.password2;

            if(!usernameValid) {
                $scope.conflicts.push("Username can contain characters, numbers and underscores");
            }

            if (!emailValid) {
                $scope.conflicts.push("Invalid Email Address");
            }

            if (!passwordsMatch) {
                $scope.conflicts.push("Passwords do not match");
            }

            var routeParams = $location.search();

            if(routeParams.referral) {
                credentials.referral = routeParams.referral;
            }

            if (emailValid && passwordsMatch && usernameValid) {
                AuthService.signup(credentials, function (success) {
                    ToastService.showDevwarsToast("fa-check-circle", "Sign Up Success", "We've sent you an email, it should arrive shortly", 5000);

                    $mdDialog.cancel();
                }, function (error) {
                    console.log(error);

                    if (error.status === 409) {
                        $scope.conflicts = error.data;
                    }
                });
            }
        }
    }]);
/**
 * Created by Terence on 4/6/2015.
 */
angular.module("app.devwarsToast", [])
    .controller("DevWarsToastController", ["$scope", "$mdDialog", "faIcon", "title", "message", function ($scope, $mdDialog, faIcon, title, message) {
        $scope.faIcon = faIcon;
        $scope.title = title;
        $scope.message = message;
    }]);