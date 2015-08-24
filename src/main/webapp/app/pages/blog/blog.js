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
    .controller("BlogController", ["$scope", "$stateParams", "BlogService", "$location", function ($scope, $stateParams, BlogService, $location) {

        console.log($stateParams);
        if($stateParams.title)
        {
            BlogService.getBlog($stateParams.title, function (success) {
                $scope.post = success.data;
            }, angular.noop);
        } else $location.path('/');

    }]);
