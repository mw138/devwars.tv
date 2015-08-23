angular.module("app.blogList", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('blog', {
                    url: '/blog/:title',
                    templateUrl: '/app/pages/blog/blogView.html',
                    controller: "BlogController"
                });
        }])
    .controller("BlogController", function ($scope, $stateParams) {

    });
