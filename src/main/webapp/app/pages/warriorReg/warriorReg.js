angular.module("app.warriorReg", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('warriorReg', {
                    url: '/warrior-signup',
                    templateUrl: '/app/pages/warriorReg/warriorRegView.html',
                    controller: "WarriorRegController",

                    auth: true
                });

        }])
    .controller("WarriorRegController", ["$scope", "AuthService", "WarriorService", "ToastService", "$http", "$state", "$filter", function ($scope, AuthService, WarriorService, ToastService, $http, $state, $filter) {

        $scope.AuthService = AuthService;

        $scope.warrior = {};

        $scope.warrior.htmlRate = 1;
        $scope.warrior.cssRate = 1;
        $scope.warrior.jsRate = 1;

        $scope.initWarrior = function () {
            if(!AuthService.user) {
                AuthService.callbacks.push($scope.initWarrior);
            } else {
                if(!AuthService.user.warrior) $state.go('warriorReg');

                $scope.warrior = AuthService.user.warrior;

                var warrior = $scope.warrior;

                if(warrior) {
                    warrior.month = $filter('date')(warrior.dob, 'MM');
                    warrior.day = $filter('date')(warrior.dob, 'dd');
                    warrior.year = $filter('date')(warrior.dob, 'yyyy');
                    warrior.email = AuthService.user.email;
                    $scope.hasTS = true;
                }
            }
        };

        $scope.skillNames = ["Novice", "Beginner", "Intermediate", "Advanced", "Expert"];

        $scope.htmlTooltips = ["You have a basic understanding of HTML, and can use essential tags.", "You have a working knowledge of HTML. You understand the basic syntax and methodology of ordering and adding elements to the DOM (Document Object Model).", "You have a strong understanding of HTML, and ability to structure a webpage. You have a knowledge of a variety of attributes, and know how to use them correctly. You have a strong understanding of semantics and structural meaning (parameter usage) .", "You have worked on larger scale projects, and collaborated with other people. You have strong experience working on various development projects. You have worked with SEO tools.", "You use HTML at a production level. You can create websites from scratch with no use of frameworks, and have worked as a front-end developer. You have used Shadow DOM in the past."];
        $scope.cssTooltips = ["You have basic knowledge of CSS. You understand the basic syntax and methodology of cascading stylesheets", "You have a working knowledge of CSS. You have style elements, and know about different frameworks. You have used Bootstrap. You have heard of pre-processors. ", "You have extensive CSS knowledge. You have experience working with responsive design, and understand the mobile-first ideology. You understand the issues that can occur with different browsers. You have used a CMS in the past, and can style elements on them ", "You have worked on larger scale projects, and collaborated with other people. You have strong experience working on various development projects. You understand BEM practices, and implement them ", "You use CSS at a production level. You can style websites from scratch with no use of frameworks, and have worked as a front-end developer"];
        $scope.jsTooltips = ["You have a basic knowledge of JavaScript.", "You have working knowledge of JavaScript. You understand the basic syntax and methodology of OOP", "You have used Javascript with a popular library or framework, and used it for DOM manipulation.", "You have worked on larger scale projects, and collaborated with other people. You have strong experience working on various development projects. You have knowledge of MVC and it's use in creating web application.", "You use JavaScript at a production level. You make full use of MVC and MV Frameworks, have and incredible understaning of OOP, and have worked as a professional front-end developer"];

        $scope.htmlTooltip = $scope.htmlTooltips[0];
        $scope.cssTooltip = $scope.cssTooltips[0];
        $scope.jsTooltip = $scope.jsTooltips[0];

            $scope.getRange = function (start, finish) {
            var returnArray = [];

            for(var i = start; i <= finish; i++) {
                returnArray.push(i);
            }

            return returnArray;
        };

        $scope.setHtml = function (rate) {
            $scope.htmlTooltip = $scope.htmlTooltips[rate - 1];
            $scope.warrior.htmlRate = rate;
        };

        $scope.setCSS = function (rate) {
            $scope.cssTooltip = $scope.cssTooltips[rate - 1];
            $scope.warrior.cssRate = rate;
        };

        $scope.setJS = function (rate) {
            $scope.jsTooltip = $scope.jsTooltips[rate - 1];
            $scope.warrior.jsRate = rate;
        };

        $scope.register = function (warrior) {
            if($scope.hasTS) {
                $http({
                    url: "/v1/warrior/register",
                    method: "GET",
                    params: warrior
                })
                    .then(function (success) {
                        ToastService.showDevwarsToast("fa-check-circle", "Success", "Registered to be a warrior");

                        setTimeout(function () {
                            location.href = "/";
                        }, 2000);
                    }, function (error) {
                        if (error.status === 400) {
                            ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "Missing Parameters");
                        } else {
                            ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
                        }
                    });
            } else {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", "You must have Teamspeak to be a warrior");
            }
        };

        $scope.update = function (warrior) {
            warrior.dob = new Date(warrior.year, warrior.month - 1, warrior.day).getTime();

            WarriorService.updateWarrior(JSON.stringify(warrior), function (success) {
                ToastService.showDevwarsToast("fa-check-circle", "Success", "Updated Warrior");
            }, function (error) {
                ToastService.showDevwarsErrorToast("fa-exclamation-circle", "Error", error.data);
            });
        };

        $scope.initWarrior();

    }]);