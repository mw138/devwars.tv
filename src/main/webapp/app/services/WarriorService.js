angular.module('app.WarriorService', [])
    .factory('WarriorService', ['$http', function ($http) {
        var WarriorService = {};
        WarriorService.http = {};

        /*
         Required Role : PENDING
         Query Parameter {cssRate} : int
         Query Parameter {year} : int
         Query Parameter {about} : java.lang.String
         Query Parameter {htmlRate} : int
         Query Parameter {firstName} : java.lang.String
         Query Parameter {month} : int
         Query Parameter {jsRate} : int
         Query Parameter {c9Name} : java.lang.String
         Query Parameter {location} : java.lang.String
         Query Parameter {company} : java.lang.String
         Query Parameter {favFood} : java.lang.String
         Query Parameter {favTool} : java.lang.String
         Query Parameter {day} : int
         Query Parameter {email} : java.lang.String
         */
        WarriorService.register = function (cssRate, year, about, htmlRate, firstName, month, jsRate, c9Name, location, favFood, favTool, day, email, company, successCallback, errorCallback) {
            $http({
                method: 'GET',
                url: '/v1/warrior/register',
                params: {
                    cssRate: cssRate,
                    year: year,
                    about: about,
                    htmlRate: htmlRate,
                    firstName: firstName,
                    month: month,
                    jsRate: jsRate,
                    c9Name: c9Name,
                    location: location,
                    company: company,
                    favFood: favFood,
                    favTool: favTool,
                    day: day,
                    email: email
                }
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        WarriorService.http.register = function (cssRate, year, about, htmlRate, firstName, month, jsRate, c9Name, location, favFood, favTool, day, email, company, successCallback, errorCallback) {
            return $http({
                method: 'GET',
                url: '/v1/warrior/register',
                params: {
                    cssRate: cssRate,
                    year: year,
                    about: about,
                    htmlRate: htmlRate,
                    firstName: firstName,
                    month: month,
                    jsRate: jsRate,
                    c9Name: c9Name,
                    location: location,
                    company: company,
                    favFood: favFood,
                    favTool: favTool,
                    day: day,
                    email: email
                }
            })
        };
        /*
         Required Role : USER
         */
        WarriorService.updateWarrior = function (warrior, successCallback, errorCallback) {
            $http({
                method: 'POST',
                url: '/v1/warrior/update',
                data: {warrior: warrior}
            })
                .then(function (success) {
                        successCallback(success)
                    },
                    function (error) {
                        errorCallback(error)
                    });

        };

        WarriorService.http.updateWarrior = function (warrior, successCallback, errorCallback) {
            return $http({
                method: 'POST',
                url: '/v1/warrior/update',
                data: {warrior: warrior}
            })
        };
        return WarriorService;
    }]);
