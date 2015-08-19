angular.module('app.PlayerService', [])
    .factory('PlayerService', ['$http', function($http){
        var PlayerService = {};

        /*
         Required Role : ADMIN
         Query Parameter {teamID} : int
         Query Parameter {language} : java.lang.String
         */
        PlayerService.addPlayer = function(teamID, language, user, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/player/add',
                data: {teamID : teamID,language : language,user : user}
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
         Path Variable {playerID} : int
         */
        PlayerService.removePlayer = function(playerID, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/player/' + playerID + '/remove',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        return PlayerService;
    }]);
