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
    .controller("TeamController", ["$scope", "$mdDialog", "UserService", "UserTeamService", "AuthService", function ($scope, $mdDialog, UserService, UserTeamService, AuthService) {

        //console.log("UserService.getMyTeam():", UserService.getMyTeam());

        //Set team to null (the false state)
        $scope.team = null;
        $scope.isOwner = false;

        UserService.http.getMyTeam()
            .then(function (success) {
                $scope.team = success.data;

                $scope.isOwner = ($scope.team.owner.id === AuthService.user.id);
            }, angular.noop);


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