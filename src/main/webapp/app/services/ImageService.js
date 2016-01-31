angular.module('app.ImageService', [])
    .factory('ImageService', ['$http', function ($http) {
        var ImageService = {};
        ImageService.http = {};

        return ImageService;
    }]);
