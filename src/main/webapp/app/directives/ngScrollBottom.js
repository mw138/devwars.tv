angular.module('app.scroll-bottom', [])
    .directive('ngScrollBottom', function () {
        return {
            restrict: "A",

            link: function ($scope, $element, $attributes, $parse) {
                $element.bind('scroll', function (event) {
                    if (event.target.scrollTop >= (event.target.scrollHeight - event.target.offsetHeight)) {
                        $scope.$apply(function () {
                            $scope.$eval($attributes.ngScrollBottom);
                        })
                    }
                })
            }
        }
    });
