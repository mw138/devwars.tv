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
