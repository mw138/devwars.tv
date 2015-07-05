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
