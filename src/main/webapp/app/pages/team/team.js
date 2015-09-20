angular.module("app.team", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('team', {
                    url: '/team',
                    templateUrl: '/app/pages/team/teamView.html',
                    controller: "TeamController"
                });

        }])
    .controller("TeamController", ["$scope", "$mdDialog", "UserService", "UserTeamService", function ($scope, $mdDialog, UserService, UserTeamService) {

        //console.log("UserService.getMyTeam():", UserService.getMyTeam());

        //We must get user team status: leader, member, or false (not a member nor leader).
        $scope.userTeamStatus = false;
        $scope.team = {};

        UserService.getOwnedTeam(function (data) {
            console.log("data:", data);
            if (data) {
                $scope.userTeamStatus = 'leader';
                return $scope.team = data.data;
            }
            
        }, function (err) {

            console.log("err:", err);
            if (err.data === 'You don\'t own a team') {

                UserService.getMyTeam(function (data) {

                    console.log("data getMyTeam:", data);

                    $scope.userTeamStatus = 'member';

                }, function (err) {
                    console.log("err getMyTeam:", err);
                    if (err.data === 'You don\'t belong to a team') {

                        $scope.userTeamStatus = false;

                    }
                })

            }

        });


        /**
         * Dialogs
         */
        
        $scope.createTeam = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/createTeamDialog/createTeamDialogView.html",
                controller: "CreateTeamDialogController"
            });
        };

        $scope.invitePlayer = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/invitePlayerDialog/invitePlayerDialogView.html",
                controller: "CreateTeamDialogController"
            });
        };

        $scope.leaveTeam = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/leaveTeamDialog/leaveTeamDialogView.html",
                controller: "CreateTeamDialogController"
            });
        };

        $scope.disbandTeam = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/disbandTeamDialog/disbandTeamDialogView.html",
                controller: "CreateTeamDialogController"
            });
        }






    }]);