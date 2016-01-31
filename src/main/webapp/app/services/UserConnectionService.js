angular.module('app.UserConnectionService', [])
    .factory('UserConnectionService', ['$http', function ($http) {
        var UserConnectionService = {};
        UserConnectionService.http = {};

        /*
         Required Role : PENDING
         Query Parameter {oauth_verifier} : java.lang.String
         Query Parameter {oauth_token} : java.lang.String
         */
        UserConnectionService.twitterCallback = function (oauth_verifier, oauth_token, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/twitter_callback',
                params: {oauth_verifier: oauth_verifier, oauth_token: oauth_token}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.twitterCallback = function (oauth_verifier, oauth_token, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/twitter_callback',
                params: {oauth_verifier: oauth_verifier, oauth_token: oauth_token}
            })
        };
        /*
         Required Role : PENDING
         Query Parameter {code} : java.lang.String
         */
        UserConnectionService.twitchCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/twitch_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.twitchCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/twitch_callback',
                params: {code: code}
            })
        };
        /*
         Required Role : PENDING
         Query Parameter {code} : java.lang.String
         */
        UserConnectionService.googleCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/google_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.googleCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/google_callback',
                params: {code: code}
            })
        };
        /*
         */
        UserConnectionService.twitchAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/twitch',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.twitchAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/twitch',
                params: {}
            })
        };
        /*
         */
        UserConnectionService.facebookAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/facebook',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.facebookAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/facebook',
                params: {}
            })
        };
        /*
         Required Role : PENDING
         Query Parameter {code} : java.lang.String
         */
        UserConnectionService.facebookCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/facebook_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.facebookCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/facebook_callback',
                params: {code: code}
            })
        };
        /*
         Required Role : PENDING
         Query Parameter {code} : java.lang.String
         */
        UserConnectionService.githubCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/github_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.githubCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/github_callback',
                params: {code: code}
            })
        };
        /*
         */
        UserConnectionService.githubAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/github',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.githubAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/github',
                params: {}
            })
        };
        /*
         */
        UserConnectionService.redditAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/reddit',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.redditAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/reddit',
                params: {}
            })
        };
        /*
         Required Role : PENDING
         Query Parameter {code} : java.lang.String
         */
        UserConnectionService.redditCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/reddit_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.redditCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/reddit_callback',
                params: {code: code}
            })
        };
        /*
         */
        UserConnectionService.googleAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/google',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.googleAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/google',
                params: {}
            })
        };
        /*
         Query Parameter {email} : java.lang.String
         */
        UserConnectionService.twitterAuth = function (email, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/twitter',
                params: {email: email}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.twitterAuth = function (email, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/twitter',
                params: {email: email}
            })
        };
        /*
         Required Role : PENDING
         Path Variable {provider} : java.lang.String
         */
        UserConnectionService.disconnectAccount = function (provider, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/connect/' + provider + '/disconnect',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        UserConnectionService.http.disconnectAccount = function (provider, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/connect/' + provider + '/disconnect',
                params: {}
            })
        };
        return UserConnectionService;
    }]);
