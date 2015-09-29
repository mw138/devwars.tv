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
    .controller("TeamController", ["$scope", "$mdDialog", "UserService", "UserTeamService", "AuthService", "ToastService", "DialogService", "$http", function ($scope, $mdDialog, UserService, UserTeamService, AuthService, ToastService, DialogService, $http) {


        //Set team to null (the false state)
        $scope.team = null;
        $scope.isOwner = false;

        $scope.updateMyTeam = function() {
            UserService.http.getMyTeam()
                .then(function (success) {
                    $scope.team = success.data;
                    $scope.isOwner = ($scope.team.owner.id === AuthService.user.id);

                    //get match history.
                    UserTeamService.http.getHistory(success.data.id, null, null)
                        .then(function (success) {
                            $scope.team.matchHistory = success.data;
                        }, angular.noop);

                    UserTeamService.http.getStatistics($scope.team.id)
                        .then(function (success) {
                            $scope.statistics = success.data;
                        }, angular.noop);
                }, angular.noop);

            UserService.http.getMyTeamInvites()
                .then(function (success) {
                    $scope.invites = success.data;
                }, angular.noop);
        };

        /**
         * Roster
         */
        $scope.editingRoster = false;

        $scope.editRoster = function () {//toggle function.
            $scope.editingRoster = !$scope.editingRoster;
        };

        //removing player
        $scope.removePlayer = function (member, idx, $event) {
            if ($scope.isOwner && $scope.editingRoster) {

                //see if owner is trying to remove himself.
                if ($scope.isOwner && member.id === AuthService.user.id) {
                    return $scope.disbandTeam();
                } else {

                    DialogService.getConfirmationDialog("Confirmation", "Are you sure you want to kick " + member.username + " from " + $scope.team.name, "Yes", "No", $event)
                        .then(function () {
                            UserTeamService.http.kickUser($scope.team.id, member.id)
                                .then(function (success) {
                                    ToastService.showDevwarsToast("fa-check-circle", "Success", "Kicked " + member.username + " from team");
                                }, angular.noop);
                        }, angular.noop);

                }
            }
        };


        /**
         * Dialogs
         */

        $scope.joinTeam = function (team) {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/confirmDialog/confirmationDialogView.html",
                controller: "ConfirmDialogController",

                locals: {
                    title: "Team",
                    message: "Invitation to join " + team.name,
                    yes: "Accept",
                    no: "Decline"
                }
            })
                .then(function () {
                    console.log("accept");
                    UserTeamService.http.acceptInvite(team.id)
                        .then(function (success) {
                            console.log("success acceptInvite:", success);
                            ToastService.showDevwarsToast("fa-check-circle", "Success", "Joined Team");
                            team.members.push(AuthService.user);
                            $scope.team = team;


                        }, angular.noop)
                }, function () {
                    console.log("decline");
                })

        };

        $scope.createTeam = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/createTeamDialog/createTeamDialogView.html",
                controller: "CreateTeamDialogController"
            })
                .then(function (team) {
                    console.log(team);
                    var fd = new FormData();

                    fd.append('image', team.image);
                    fd.append('name', team.name);
                    fd.append('tag', team.tag);

                    $http.post("/v1/teams/create", fd, {
                        withCredentials: true,
                        headers: {'Content-Type': undefined},
                        transformRequest: angular.identity
                    })
                        .then(function (success) {
                            console.log("success", success);
                            $scope.team = success.data;

                        }, function (error) {
                            console.log("error", error);
                        })


                });
        };

        $scope.confirmTeamSignup = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/confirmTeamSignupDialog/confirmTeamSignupDialogView.html",
                controller: "ConfirmTeamSignupDialogController"
            })
        };

        $scope.invitePlayer = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/invitePlayerDialog/invitePlayerDialogView.html",
                controller: "InvitePlayerDialogController"
            })
                .then(function (player) {
                    console.log("player invite:", player);
                    UserTeamService.http.invitePlayer($scope.team.id, player.id)
                        .then(function (success) {
                            console.log("success invite:", success);
                            $scope.team.invites.push(player);
                            ToastService.showDevwarsToast("fa-check-circle", "Success", "Invite Sent");

                        }, function (error) {
                            console.log("error invite:", error);
                            ToastService.showDevwarsToast("fa-exclamation-circle", "Error", error.data);
                        })
                })
        };

        $scope.leaveTeam = function (team) {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/confirmDialog/confirmationDialogView.html",
                controller: "ConfirmDialogController",

                locals: {
                    title: "Team",
                    message: "Leave " + team.name,
                    yes: "Yes",
                    no: "No"
                }
            })
                .then(function () {
                    UserTeamService.http.leaveTeam(team.id)
                        .then(function (success) {
                            ToastService.showDevwarsToast("fa-check-circle", "Success", "Left Team");
                            $scope.team = null;
                        }, function (error) {
                            ToastService.showDevwarsToast("fa-exclamation-circle", "Error", error.data);
                        })
                }, angular.noop)
        };

        $scope.disbandTeam = function () {
            $mdDialog.show({
                templateUrl: "app/components/dialogs/disbandTeamDialog/disbandTeamDialogView.html",
                controller: "DisbandTeamDialogController",

                locals: {
                    team: $scope.team
                }
            })
                .then(function (team) {
                    UserTeamService.http.deleteTeam($scope.team.id, $scope.team.name)
                        .then(function (success) {
                            ToastService.showDevwarsToast("fa-check-circle", "Success", "Team Disbanded");

                            $scope.team = null;
                        }, function (error) {
                            ToastService.showDevwarsToast("fa-exclamation-circle", "Error", error.data);

                        })
                }, angular.noop);
        };

        $scope.$watch('chosenImage', function (newVal, oldVal) {
            if(newVal !== oldVal) {
                DialogService.getBase64ForImage($scope.chosenImage)
                    .then(function (image) {
                        var fd = new FormData();
                        fd.append("image", image.blob);

                        $http.post('/v1/teams/' + $scope.team.id + '/avatar', fd, {
                            withCredentials: true,
                            headers: {'Content-Type': undefined},
                            transformRequest: angular.identity
                        })
                            .then(function (success) {
                                location.reload();
                            }, angular.noop);
                    }, angular.noop);
            }
        });

        //Only run once we have successfully fetched user data
        AuthService.isLoggedIn()
            .then($scope.updateMyTeam, angular.noop);

    }]);