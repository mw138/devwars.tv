angular.module('app.PlayerService', [])
    .factory('PlayerService', ['$http', function ($http) {
        var PlayerService = {};
        PlayerService.http = {};

        /*
         Required Role : ADMIN
         Path Variable {playerID} : int
         */
        PlayerService.removePlayer = function (playerID, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/player/' + playerID + '/remove',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        PlayerService.http.removePlayer = function (playerID, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/player/' + playerID + '/remove',
                params: {}
            })
        };
        /*
         Required Role : ADMIN
         Query Parameter {teamID} : int
         Query Parameter {language} : java.lang.String
         */
        PlayerService.addPlayer = function (teamID, language, user, successCallback, errorCallback) {
            $http({
                method: 'POST',
                url: '/v1/player/add',
                data: {teamID: teamID, language: language, user: user}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        PlayerService.http.addPlayer = function (teamID, language, user, successCallback, errorCallback) {
            return $http({
                method: 'POST',
                url: '/v1/player/add',
                data: {teamID: teamID, language: language, user: user}
            })
        };
        return PlayerService;
    }]);
