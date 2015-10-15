angular.module('app.socketService', [])
    .factory('SocketService', function ($q) {
        var SocketService = {};

        SocketService._io = null;

        SocketService.init = function (url, cb) {
            SocketService._io = io(url);
            SocketService._io.on('connect', cb);
        };

        SocketService.emit = function (event, data) {
            var deferred = $q.defer();

            SocketService._io.emit('global', {
                event: 'some',
                data: JSON.stringify(data)
            }, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        };

        SocketService.on = function (event) {
            var deferred = $q.defer();

            SocketService._io.on(event, function (data) {
                deferred.resolve(data);
            });

            return deferred.promise;
        };
        
        return SocketService;
    });