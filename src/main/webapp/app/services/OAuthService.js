angular.module('app.OAuthService', [])
    .factory('OAuthService', ['$http', function ($http) {
        var OAuthService = {};
        OAuthService.http = {};

        /*
         */
        OAuthService.redditAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/reddit',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.redditAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/reddit',
                params: {}
            })
        };
        /*
         Query Parameter {code} : java.lang.String
         */
        OAuthService.redditCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/reddit_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.redditCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/reddit_callback',
                params: {code: code}
            })
        };
        /*
         */
        OAuthService.googleAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/google',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.googleAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/google',
                params: {}
            })
        };
        /*
         Query Parameter {code} : java.lang.String
         */
        OAuthService.googleCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/google_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.googleCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/google_callback',
                params: {code: code}
            })
        };
        /*
         Query Parameter {email} : java.lang.String
         */
        OAuthService.twitterAuth = function (email, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/twitter',
                params: {email: email}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.twitterAuth = function (email, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/twitter',
                params: {email: email}
            })
        };
        /*
         Query Parameter {oauth_verifier} : java.lang.String
         Query Parameter {oauth_token} : java.lang.String
         */
        OAuthService.twitterCallback = function (oauth_verifier, oauth_token, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/twitter_callback',
                params: {oauth_verifier: oauth_verifier, oauth_token: oauth_token}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.twitterCallback = function (oauth_verifier, oauth_token, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/twitter_callback',
                params: {oauth_verifier: oauth_verifier, oauth_token: oauth_token}
            })
        };
        /*
         Query Parameter {code} : java.lang.String
         */
        OAuthService.githubCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/github_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.githubCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/github_callback',
                params: {code: code}
            })
        };
        /*
         */
        OAuthService.twitchAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/twitch',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.twitchAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/twitch',
                params: {}
            })
        };
        /*
         */
        OAuthService.facebookAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/facebook',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.facebookAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/facebook',
                params: {}
            })
        };
        /*
         Query Parameter {code} : java.lang.String
         */
        OAuthService.facebookCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/facebook_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.facebookCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/facebook_callback',
                params: {code: code}
            })
        };
        /*
         */
        OAuthService.githubAuth = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/github',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.githubAuth = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/github',
                params: {}
            })
        };
        /*
         Query Parameter {code} : java.lang.String
         */
        OAuthService.twitchCallback = function (code, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/oauth/twitch_callback',
                params: {code: code}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        OAuthService.http.twitchCallback = function (code, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/oauth/twitch_callback',
                params: {code: code}
            })
        };
        return OAuthService;
    }]);
