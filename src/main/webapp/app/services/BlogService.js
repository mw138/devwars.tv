angular.module('app.BlogService', [])
    .factory('BlogService', ['$http', function($http){
        var BlogService = {};

        /*
         Required Role : ADMIN
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

        /*
         Required Role : ADMIN
         Path Variable {id} : int
         Query Parameter {image_url} : java.lang.String
         Query Parameter {description} : java.lang.String
         Query Parameter {text} : java.lang.String
         Query Parameter {title} : java.lang.String
         */
        BlogService.updateBlog = function(id, image_url, description, text, title, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/' + id + '/update',
                params: {image_url : image_url,description : description,text : text,title : title}
            })
                .then(function(success){
                    successCallback(success)
                },
                function(error){
                    errorCallback(error)
                });

        };

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
         Required Role : ADMIN
         Query Parameter {image_url} : java.lang.String
         Query Parameter {description} : java.lang.String
         Query Parameter {text} : java.lang.String
         Query Parameter {title} : java.lang.String
         */
        BlogService.createBlog = function(image_url, description, text, title, successCallback, errorCallback){
            $http({
                method: 'GET',
                url: '/v1/blog/create',
                params: {image_url : image_url,description : description,text : text,title : title}
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
