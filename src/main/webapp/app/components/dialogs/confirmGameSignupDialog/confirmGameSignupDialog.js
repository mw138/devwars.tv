/**
 * Created by Terence on 4/5/2015.
 */
angular.module("app.gameSignupConfirm", [])
    .controller("GameSignupConfirmDialogController", ["$scope", "$mdDialog", "game", function ($scope, $mdDialog, game) {
        $scope.game = game;
        $scope.$mdDialog = $mdDialog;

        $scope.confirmSignUp = function () {
            $mdDialog.hide({
                game: game
            });
        };

        $scope.cancelSignUp = function () {
            $mdDialog.cancel();
        };
    }]);