angular.module("app.badges", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('badges', {
                    url: '/badges',
                    templateUrl: '/app/pages/badges/badgesView.html',
                    controller: "BadgesController"
                });

        }])
    .controller("BadgesController", ["$scope", "$http", "BadgeService", "AuthService", function ($scope, $http, BadgeService, AuthService) {

        $scope.imageNameFromName = function (name) {
            return name.toLowerCase().replace(/\s+/g, "-");
        };

        $scope.userHasBadge = function (badge) {
            if(AuthService.user) {
                for(var badgeKey in AuthService.user.badges) {
                    var currentBadge = AuthService.user.badges[badgeKey];

                    if(badge.id === currentBadge.id) return true;
                }
            }

            return false;
        };

        $scope.badgeForName = function (name) {
            for(var badgeKey in $scope.badges) {
                var badge = $scope.badges[badgeKey];

                if (badge.name === name) return badge;
            }

            return null;
        };

        BadgeService.getAll(function (success) {
            $scope.badges = success.data.badges;
            $scope.userCount = success.data.userCount;
        }, angular.noop);

        $scope.badgePercentCalculators = {
            "Authentic" : function () {
                return AuthService.user.role == "PENDING" ? 0 : 100;
            },

            "Making Links" : function () {
                return Math.min(AuthService.user.connectedAccounts.length > 0 ? 100 : 0, 100);
            },

            "Full Coverage" : function () {
                return Math.min((AuthService.user.connectedAccounts.length / 5.0) * 100, 100);
            },

            "Feed The Pig" : function () {
                return Math.min((AuthService.user.ranking.points / 5000.0) * 100, 100);
            },

            "Penny-Pincher": function () {
                return Math.min((AuthService.user.ranking.points / 25000.0) * 100, 100);
            },

            "Follow Me": function () {
                return Math.min((AuthService.user.referredUsers / 5.0) * 100, 100);
            },

            "Influential": function () {
                return Math.min((AuthService.user.referredUsers / 25.0) * 100, 100);
            },

            "Natural Leader": function () {
                return Math.min((AuthService.user.referredUsers / 50.0) * 100, 100);
            },

            "Ace High": function () {
                return $scope.userHasBadge($scope.badgeForName("Ace High")) ? 100 : 0;
            },

            "Beginner's Luck": function () {
                return AuthService.user.gamesWon > 0 ? 100 : 0;
            },

            "Victorious" : function () {
                return Math.min((AuthService.user.gamesWon / 5.0) * 100, 100);
            },

            "Hotshot" : function () {
                return Math.min((AuthService.user.gamesWon / 10.0) * 100, 100);
            },

            "Steamroller" : function () {
                return Math.min((AuthService.user.gamesWon / 25.0) * 100, 100);
            },

            "Hot Streak": function () {
                return Math.min(AuthService.user.gameStreak / 3.0 * 100, 100);
            },

            "Cake Day": function () {
                return $scope.userHasBadge($scope.badgeForName("Cake Day")) ? 100 : 0;
            },

            "High Roller": function () {
                return Math.min(AuthService.user.bettingBitsEarned / 10000.0 * 100, 100);
            },

            "First Timer" : function () {
                return Math.min(AuthService.user.gamesWatched / 1 * 100, 100);
            },

            "Hobbyist" : function () {
                return Math.min(AuthService.user.gamesWatched / 5 * 100, 100);
            },

            "Biggest Fan" : function () {
                return Math.min(AuthService.user.gamesWatched / 25 * 100, 100);
            },

            "Obsessed" : function () {
                return Math.min(AuthService.user.gamesWatched / 50 * 100, 100);
            }
        }

    }]);