angular.module('app.UserTeamService', [])
    .factory('UserTeamService', ['$http', function($http){
        var UserTeamService = {};
        UserTeamService.http = {};

        /*
         Required Role : USER
         Query Parameter {name} : java.lang.String
         */
        UserTeamService.createTeam = function(name, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/teams/create',
                params: {name : name}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserTeamService.http.createTeam = function(name, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/teams/create',
                params: {name : name}
            })};/*
         Required Role : PENDING
         Path Variable {id} : int
         Query Parameter {user} : int
         */
        UserTeamService.invitePlayer = function(id, user, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/invite',
                params: {user : user}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserTeamService.http.invitePlayer = function(id, user, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/invite',
                params: {user : user}
            })};/*
         Required Role : PENDING
         Path Variable {id} : int
         */
        UserTeamService.acceptInvite = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/invite/accept',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserTeamService.http.acceptInvite = function(id, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/invite/accept',
                params: {}
            })};/*
         Path Variable {id} : int
         Query Parameter {count} : int
         Query Parameter {page} : int
         */
        UserTeamService.getHistory = function(id, count, page, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/history',
                params: {count : count,page : page}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserTeamService.http.getHistory = function(id, count, page, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/history',
                params: {count : count,page : page}
            })};/*
         Path Variable {id} : int
         */
        UserTeamService.getStatistics = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/statistics',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserTeamService.http.getStatistics = function(id, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/statistics',
                params: {}
            })};/*
         Required Role : USER
         Path Variable {id} : int
         Query Parameter {name} : java.lang.String
         */
        UserTeamService.deleteTeam = function(id, name, successCallback, errorCallback){
            $http({
                method: 'POST',
                url: '/v1/teams/' + id + '/delete',
                data: {name : name}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserTeamService.http.deleteTeam = function(id, name, successCallback, errorCallback){
            return $http({
                method: 'POST',
                url: '/v1/teams/' + id + '/delete',
                data: {name : name}
            })};/*
         Path Variable {id} : int
         */
        UserTeamService.getTeam = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/teams/' + id + '',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserTeamService.http.getTeam = function(id, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/teams/' + id + '',
                params: {}
            })};/*
         Required Role : USER
         Path Variable {id} : int
         Query Parameter {newName} : java.lang.String
         */
        UserTeamService.editTeamName = function(id, newName, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/changename',
                params: {newName : newName}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        UserTeamService.http.editTeamName = function(id, newName, successCallback, errorCallback){
            return $http({
                method: 'GET',
                url: '/v1/teams/' + id + '/changename',
                params: {newName : newName}
            })};return UserTeamService;
    }]);
