angular.module('app.ngCopyToClipboard', [])
.directive('ngCopyToClipboard', function () {
    return {
        restrict: 'A',

        scope: {
            'copy': '=ngCopyToClipboard'
        },

        link: function (scope, element, attributes) {
            element.bind('click', function (event) {
                window.prompt("Press Ctrl-C to copy then press Enter", scope.copy);
            })
        }
    }
})