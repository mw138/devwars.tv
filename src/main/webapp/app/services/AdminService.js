angular.module('app.AdminService', [])
    .factory('AdminService', ['$http', function ($http) {
        var AdminService = {};
        AdminService.http = {};

        /*
         Required Role : ADMIN
         */
        AdminService.archivePhotos = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/admin/photoarchive',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        AdminService.http.archivePhotos = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/admin/photoarchive',
                params: {}
            })
        };
        /*
         Required Role : ADMIN
         */
        AdminService.getArchiveFolderList = function (successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/admin/photoarchivelist',
                params: {}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        AdminService.http.getArchiveFolderList = function (successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/admin/photoarchivelist',
                params: {}
            })
        };
        return AdminService;
    }]);
