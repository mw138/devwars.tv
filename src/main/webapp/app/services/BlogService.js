angular.module('app.BlogService', [])
    .factory('BlogService', ['$http', function($http){
        var BlogService = {};

        /*
         */
        BlogService.allPosts = function(successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/all',
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
         Required Role : BLOGGER
         */
        BlogService.createBlog = function(post, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/create',
                params: {post : post}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Required Role : BLOGGER
         Path Variable {id} : int
         */
        BlogService.deleteBlog = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/' + id + '/delete',
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
         Required Role : BLOGGER
         Path Variable {id} : int
         */
        BlogService.updateBlog = function(id, post, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/' + id + '/update',
                params: {post : post}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        /*
         Path Variable {id} : int
         */
        BlogService.getBlog = function(id, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/' + id + '',
                params: {}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

        return BlogService;
    }]);
