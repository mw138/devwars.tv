var app = angular.module('app', [
// modules
    'app.coming',
    'app.AuthService',
    'app.header',
    'app.home',
    'app.signup',
    'app.gameControlPanel',
    'app.GameService',
    'app.PlayerService',
    'app.BlogService',
    'app.InfoService',
    'app.UserService',
    'app.WarriorService',
    'app.ContactService',
    'app.games',
    'app.gameSignupConfirm',
    'app.editPlayerDialog',
    'app.addPlayerDialog',
    'app.addSelectedPlayerDialog',
    'app.createTeamDialog',
    'app.invitePlayerDialog',
    'app.leaveTeamDialog',
    'app.disbandTeamDialog',
    'app.addBlogPostDialog',
    'app.InputDialog',
    'app.devwarsToast',
    'app.toastService',
    'app.BadgeService',
    'app.blog',
    'app.user',
    'pickadate',
    'app.contact',
    'app.about',
    'app.dashboard',
    'app.badges',
    'app.warriorReg',
    'app.DialogService',
    'app.settings',
    'app.help',
    'app.liveGame',
    'app.verify',
    'app.enterDirective',
    'app.subscribeDirective',
    'app.shop',
    'app.OAuthDirective',
    'app.sidebar',
    'app.editAvatarImage',
    'app.fileread',
    'app.rank',
    'app.profile',
    'app.dashnav',
    'app.confirmDialog',
    'app.leaderboards',
    'app.modCP',
    'app.scroll-bottom',
    'app.no-scroll-other',
    'app.team',
    'app.addGame',
    'app.UserTeamService',

    //dependencies
    'ngCookies',
    'ngMaterial',
    'vcRecaptcha',
    'textAngular',
    'ngImgCrop'
]);

app.config(['$urlRouterProvider', '$httpProvider', '$locationProvider', function ($urlRouterProvider, $httpProvider, $locationProvider) {
    // all page specific routes are in their js file
    $urlRouterProvider.otherwise('/');

    if(false) {
        $httpProvider.interceptors.push(function () {

            var Interceptor = {};

            Interceptor.request = function (config) {
                var url = config.url;

                if (url.indexOf('.html') < 0) {
                    config.url = "http://local.bezcode.com:9090/" + config.url;
                }

                return config;
            };

            return Interceptor;

        });
    }

    $httpProvider.defaults.transformResponse = function (response) {
        try {
            return JSON.parse(response);
        } catch (exception) {
            return response;
        }
    };

    $httpProvider.defaults.transformRequest = function (obj) {
        var str = [];
        for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        return str.join("&");
    };

    $httpProvider.defaults.withCredentials = true;
    $httpProvider.defaults.headers.post['Content-Type'] = "application/x-www-form-urlencoded";

    $locationProvider.html5Mode(true);
}]);

app.filter("reverse", function () {
    return function (data) {
        return data.reverse();
    }
});

app.filter("players", function () {
    return _.memoize(function (data) {
        var langs = ["html", "css", "js"];

        data.sort(function (a) {
            return langs.indexOf(a.language.toLowerCase());
        });

        return data;
    })
});

app.filter('camel', function () {
    return function (data) {
        var result = data.replace( /([A-Z])/g, " $1" );
        var finalResult = result.charAt(0).toUpperCase() + result.slice(1);

        return finalResult;
    }
})

app.run(function ($rootScope, $location, AuthService) {
    $rootScope.$on('$stateChangeStart', function (event, toState) {
        //Is the route protected
        if(toState.auth) {
            //Are we logged in?
            AuthService.isLoggedIn()
                .then(angular.noop, function (error) {
                    $location.path('/');
                });
        }
    });
});