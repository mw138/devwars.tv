angular.module('app.smoothHref', [])
    .directive('href', function () {
        return {
            restrict: "A",

            scope: {
                href: '@'
            },

            link: function (scope, element, attrs) {
                element.bind('click', function (event) {
                    var targetID = scope.href;

                    if (targetID.charAt(0) === '#') {
                        $('html, body').animate({
                            scrollTop: $(targetID).offset().top
                        }, 500);
                    }

                    event.preventDefault();
                })
            }
        }
    });