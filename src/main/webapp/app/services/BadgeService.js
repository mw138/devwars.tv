angular.module('app.BadgeService', [])
    .factory('BadgeService', ['$http', function ($http) {
        var BadgeService = {};
        BadgeService.http = {};

        /*
         */
        BadgeService.getAll = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/badge/all',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        BadgeService.http.getAll = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/badge/all',
                params: {}
            })
        };
        /*
         Path Variable {id} : int
         */
        BadgeService.getBadge = function (id, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/badge/' + id + '',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        BadgeService.http.getBadge = function (id, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/badge/' + id + '',
                params: {}
            })
        };
        return BadgeService;
    }]);
