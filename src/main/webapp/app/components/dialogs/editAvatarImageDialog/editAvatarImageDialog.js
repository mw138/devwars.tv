angular.module("app.editAvatarImage", [])
    .controller("EditAvatarImageDialogController", ["$scope", "image", "$http", "$mdDialog", "ToastService", function ($scope, image, $http, $mdDialog, ToastService) {
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
            var fd = new FormData();
            fd.append("file", dataURItoBlob(image));

            $http.post("/v1/user/changeavatar", fd, {
                withCredentials: true,
                headers: {'Content-Type': undefined},
                transformRequest: angular.identity
            })
                .then(function (success) {
                    $mdDialog.hide();
                }, function (error) {
                    ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                });
        };

        var dataURItoBlob = function(dataURI) {
            var binary = atob(dataURI.split(',')[1]);
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
            var array = [];
            for(var i = 0; i < binary.length; i++) {
                array.push(binary.charCodeAt(i));
            }
            return new Blob([new Uint8Array(array)], {type: mimeString});
        };

    }]);