angular.module("app.dashboard", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('dashboard', {
                    name: "dashboard",
                    url: "/dashboard",
                    abstract: true,

                    templateUrl: '/app/pages/dashboard/dashboardView.html',
                    controller: "DashboardController"
                })
                .state('dashboard.home', {
                    url: '',

                    parent: "dashboard",

                    auth: true,

                    views: {
                        '' : {
                            templateUrl: '/app/pages/dashboard/dashboardView.html',
                            controller: "DashboardController"
                        },

                        'dashboardInner@dashboard' : {
                            templateUrl: '/app/pages/dashboard/dashboardHome.html',
                            controller: "DashboardController"
                        }
                    }
                })
                .state('dashboard.profile', {
                    url: '/profile',

                    parent: "dashboard",

                    auth: true,

                    views: {
                        '' : {
                            templateUrl: '/app/pages/dashboard/dashboardView.html',
                            controller: "DashboardController"
                        },

                        'dashboardInner@dashboard' : {
                            templateUrl: '/app/pages/settings/settingsProfileView.html',
                            controller: "SettingsController"
                        }
                    }
                })
                .state('dashboard.badges', {
                    url: '/badges',

                    parent: "dashboard",

                    auth: true,

                    resolve: {
                        'myBadges': ['UserService', function (UserService) {
                            return UserService.http.getBadges();
                        }],

                        'allBadges': ['BadgeService', function (BadgeService) {
                            return BadgeService.http.getAll();
                        }]
                    },

                    views: {
                        '' : {
                            templateUrl: '/app/pages/dashboard/dashboardView.html',
                            controller: "DashboardController"
                        },

                        'dashboardInner@dashboard' : {
                            templateUrl: "/app/pages/badges/badgesView.html",
                            controller: "BadgesController"
                        }
                    }
                });
        }])
    .controller("DashboardController", ["$scope", "AuthService", "UserService", function ($scope, AuthService, UserService) {
        $scope.AuthService = AuthService;

        $scope.onLoad = function () {
            UserService.getActivities(function (success) {
                AuthService.user.activityLog = success.data;
            }, angular.noop);

            UserService.getBadges(function (success) {
                AuthService.user.badges = success.data;
            }, angular.noop);
        };

        if(!AuthService.user) {
            AuthService.callbacks.push($scope.onLoad);
        } else $scope.onLoad();

        $scope.imageForRank = function (rank) {
            var mapping = {
                'Bronze I': "bronze1",
                'Bronze II': "bronze2",
                'Bronze III': "bronze3",
                'Bronze IV': "bronze4",
                'Bronze V': "bronze5",
                'Silver I':  "silver1",
                'Silver II': "silver2",
                'Silver III': "silver3",
                'Silver IV': "silver4",
                'Silver V': "silver5",
                'Gold I': "gold1",
                'Gold II': "gold2",
                'Gold III': "gold3",
                'Gold IV': "gold4",
                'Gold V': "gold5",
                'Platinum I': "platinum1",
                'Platinum II': "platinum2",
                'Platinum III': "platinum3",
                'Platinum IV': "platinum4",
                'Platinum V': "platinum5",
                'Diamond I': "diamond1",
                'Diamond II': "diamond2",
                'Diamond III': "diamond3",
                'Diamond IV': "diamond4",
                'Diamond V': "diamond5",
                'Elite': "elite"
            }

            return mapping[rank];
        }
    }]);