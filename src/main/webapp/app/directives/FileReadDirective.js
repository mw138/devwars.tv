angular.module("app.fileread", [])
    .directive('fileread', function () {
        return {
            restrict: "A",

            scope: {
                "fileread": "="
            },

            link: function ($scope, $element) {
                $element.bind("change", function ($event) {
                    var reader = new FileReader();

                    reader.onload = function (readevent) {
                        $scope.$apply(function ($scope) {
                            $scope.fileread = readevent.target.result;
                        })
                    };

                    reader.readAsDataURL($event.target.files[0]);
                })
            }
        }
    })