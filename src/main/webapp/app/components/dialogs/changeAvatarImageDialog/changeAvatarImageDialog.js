angular.module('app.changeAvatarImageDialog', [])
    .controller("ChangeAvatarImageDialogController", ["$scope", "$mdDialog", "image", function ($scope, $mdDialog, image) {
        $scope.$mdDialog = $mdDialog;

        $scope.image = image;
        $scope.myCroppedImage = '';

        $scope.uploadImage = function (image) {
            $mdDialog.hide({
                image: image,
                blob: $scope.dataURItoBlob(image)
            });
        };

        $scope.dataURItoBlob = function (dataURI) {
            var binary = atob(dataURI.split(',')[1]);
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
            var array = [];
            for (var i = 0; i < binary.length; i++) {
                array.push(binary.charCodeAt(i));
            }
            return new Blob([new Uint8Array(array)], {type: mimeString});
        };

    }]);
