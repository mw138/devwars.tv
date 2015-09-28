angular.module('app.createTeamDialog', [])
    .controller("CreateTeamDialogController", ["$scope", "$mdDialog", function ($scope, $mdDialog) {
        $scope.$mdDialog = $mdDialog;
        $scope.teamName = '';
        $scope.teamTag = '';
        $scope.teamImage = '';

        var dataURItoBlob = function(dataURI) {
            var binary = atob(dataURI.split(',')[1]);
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
            var array = [];
            for(var i = 0; i < binary.length; i++) {
                array.push(binary.charCodeAt(i));
            }
            return new Blob([new Uint8Array(array)], {type: mimeString});
        };

        $scope.editTeamImage = function (image, $event) {
            console.log("image:", image);
            $scope.image = image;
            $scope.myCroppedImage = '';

            $scope.testImage = new Image;
            $scope.testImage.src = image;

            $scope.$mdDialog = $mdDialog;

            $scope.minSize = Math.min($scope.testImage.width, $scope.testImage.height);

            setTimeout(function () {
                $scope.minSize = 50;
            }, 500);




        };


        $scope.$watch("selectedTeamImage", function (oldVal, newVal) {

            if(oldVal !== newVal)
                $scope.editTeamImage($scope.selectedTeamImage);
        });


        $scope.submitTeam = function (teamName, teamTag, croppedImage) {
            console.log(teamName, teamTag, croppedImage);

            $mdDialog.hide({
                name: teamName,
                tag: teamTag,
                image: dataURItoBlob(croppedImage)
            });

        };


    }]);