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
    .controller("TeamController", ["$scope", "$mdDialog", "UserService", "UserTeamService", "AuthService", "ToastService", "DialogService", function ($scope, $mdDialog, UserService, UserTeamService, AuthService, ToastService, DialogService) {

        //console.log("UserService.getMyTeam():", UserService.getMyTeam());

        //Set team to null (the false state)
        $scope.team = null;
        $scope.isOwner = false;

        UserService.http.getMyTeam()
            .then(function (success) {
                $scope.team = success.data;
                console.log("success.data:", success.data);
                $scope.isOwner = ($scope.team.owner.id === AuthService.user.id);

                //get match history.
                UserTeamService.http.getHistory(success.data.id, null, null)
                    .then(function(success) {
                        console.log("success.history", success.data);
                        $scope.team.matchHistory = success.data;
                    })


            }, angular.noop);

        /**
         * Roster
         */
        $scope.editingRoster = false;

        $scope.editRoster = function () {//toggle function.
            $scope.editingRoster = !$scope.editingRoster;
        };


        //removing player
        $scope.removePlayer = function (member, idx) {
            if ($scope.isOwner && $scope.editingRoster) {
                console.log("removePlayer()");

                //see if owner is trying to remove himself.
                if ($scope.team.owner.id === AuthService.user.id) {
                    return $scope.disbandTeam();
                } else {

                    DialogService.getConfirmationDialog("Confirmation", "Are you sure you want to kick " + member + " from " + $scope.team.name, "Yes", "No", $event)
                        .then(function () {

                            //TODO: removePlayer logic

                        },  angular.noop);

                }



            } else { //removePlayer not owner
                //ToastService.showDevwarsToast("fa-exclamation-circle", "Error", "Error");
            }
        };



        /**
         * Dialogs
         */
        
        $scope.createTeam = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/createTeamDialog/createTeamDialogView.html",
                controller: "CreateTeamDialogController"
            })
                .then(function (teamName) {
                    console.log("team:", teamName);

                    UserTeamService.http.createTeam(teamName)
                        .then(function (success) {
                            console.log("success:", success);
                            ToastService.showDevwarsToast("fa-check-circle", "Success", "Uploaded files for team");


                        }, function (error) {
                            ToastService.showDevwarsToast("fa-exclamation-circle", "Error", error.data);
                        })
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
                controller: "DisbandTeamDialogController",

                locals: {
                    team: $scope.team
                }
            })
                .then(function (teamId, teamName) {
                    console.log("teamId", teamId);
                    UserTeamService.http.deleteTeam(teamId, teamName)
                        .then(function (success) {
                            console.log("success:", success);
                        }, function (error) {
                            console.log("error:", error);
                        })
                });
        }






    }]);