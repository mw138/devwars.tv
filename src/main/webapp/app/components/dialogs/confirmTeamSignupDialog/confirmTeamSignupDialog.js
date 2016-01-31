angular.module('app.confirmTeamSignupDialog', [])
    .controller("ConfirmTeamSignupDialogController", ["$scope", "$mdDialog", "game", "team", function ($scope, $mdDialog, game, team) {
        $scope.$mdDialog = $mdDialog;

        $scope.game = game;
        $scope.team = team;

        $scope.selectedPlayers = [];

        $scope.langArray = ['', 'html', 'css', 'js'];

        $scope.getClassForUser = function (user) {
            var count = user.clickCount;

            return $scope.langArray[count % 4];
        };

        $scope.clickPlayer = function (user) {
            user.clickCount++;
        };

        $scope.enoughPlayers = function () {
            var count = 0;

            team.members.forEach(function (user) {
                var language = $scope.langArray[user.clickCount % 4];

                if (language) count++;

                var dupeLang = team.members.some(function (secondUser) {
                    var secondLanguage = $scope.langArray[secondUser.clickCount % 4];

                    return !!(secondLanguage == language && secondUser.id !== user.id);
                });

                if (dupeLang) count = 4;
            });

            return count == 3;
        };

        $scope.confirmSignUp = function () {
            var signupUsers = [];

            team.members.forEach(function (user) {
                var language = $scope.langArray[user.clickCount % 4];

                if (language) {
                    signupUsers.push({
                        user: {
                            id: user.id
                        },

                        language: language
                    });
                }
            });

            $mdDialog.hide(signupUsers);
        }
    }]);
