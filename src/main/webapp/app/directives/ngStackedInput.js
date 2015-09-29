angular.module('app.stackedInput', [])
    .directive("ngStackedInput", function () {
        return {
            restrict: "A",
            require: "ngModel",

            scope: {
                model: "=ngModel",
                time: "=time",
                ngStackedInput: "&"
            },

            link: function (scope, element, attrs) {
                scope.lastTime = new Date().getTime();
                scope.lastTimout = null;

                scope.$watch("model", function (newVal, oldVal) {
                    scope.input = newVal;

                    if(scope.lastTimeout) clearTimeout(scope.lastTimeout);

                    scope.lastTimeout = setTimeout(scope.ngStackedInput, scope.time);
                });
            },
        }
    });