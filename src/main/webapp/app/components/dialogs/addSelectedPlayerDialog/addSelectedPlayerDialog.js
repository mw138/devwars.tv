/**
 * Created by Terence on 7/1/2015.
 */
angular.module('app.addSelectedPlayerDialog', [])
    .controller("AddSelectedPlayerDialogController", ['$scope', 'player', 'game', '$mdDialog', function ($scope, player, game, $mdDialog) {

        $scope.$mdDialog = $mdDialog;

        $scope.game = game;

    }]);