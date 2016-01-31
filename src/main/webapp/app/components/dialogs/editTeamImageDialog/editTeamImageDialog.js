angular.module("app.editTeamImage", [])
    .controller("EditTeamImageDialogController", ["$scope", "image", "$http", "$mdDialog", function ($scope, image, $http, $mdDialog) {
        $scope.image = image;
        $scope.myCroppedImage = '';

        $scope.testImage = new Image;
        $scope.testImage.src = image;

        $scope.$mdDialog = $mdDialog;

        $scope.minSize = Math.min($scope.testImage.width, $scope.testImage.height);

        setTimeout(function () {
            $scope.minSize = 50;
        }, 500);

        $scope.uploadImage = function (image) {
            $mdDialog.hide(image);
        };

        var dataURItoBlob = function (dataURI) {
            var binary = atob(dataURI.split(',')[1]);
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
            var array = [];
            for (var i = 0; i < binary.length; i++) {
                array.push(binary.charCodeAt(i));
            }
            return new Blob([new Uint8Array(array)], {type: mimeString});
        };

    }]);
