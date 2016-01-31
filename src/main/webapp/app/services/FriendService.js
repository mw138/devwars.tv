angular.module('app.FriendService', [])
    .factory('FriendService', ['$http', function ($http) {
        var FriendService = {};
        FriendService.http = {};

        return FriendService;
    }]);
