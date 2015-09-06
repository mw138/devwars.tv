angular.module('app.no-scroll-other', [])
    .directive("ngNoScrollOther", function () {
        return {
            restrict: "A",

            link: function ($scope, $element, $attributes) {
                $($element).bind('mousewheel', function (event) {
                    var target = $element.context;

                    if(target.scrollTop <= 0 && event.originalEvent.deltaY < 0) {
                        return false;
                    }

                    if( target.scrollTop >= (target.scrollHeight - target.offsetHeight) && event.originalEvent.deltaY >= 0) {
                        return false;
                    }
                });
            }
        }
    })