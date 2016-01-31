angular.module('app.modDoc', [
        'ui.router'
    ])
    .config(['$stateProvider',
        function ($stateProvider) {

            $stateProvider
                .state('modDoc', {
                    url: '/modDoc',
                    templateUrl: 'app/pages/modDoc/modDocView.html',
                    controller: "modDocController",
                    auth: 'ADMIN'
                });
        }])
    .controller("modDocController", ["$scope", function ($scope) {
        var $sitemap = $('.right .sitemap');
        var mapPos = $sitemap.position();

        /**
         * Sidebar
         */
        $(window).scroll(function (e) {

            var windowPos = $(window).scrollTop();
            if (windowPos >= mapPos.top) $sitemap.css({position: 'fixed', top: 30});
            else $sitemap.css({position: 'relative'});
        });

        /**
         * Sidebar Titles
         */
        $(window).scroll(function (e) {

            var y = $(this).scrollTop();
            $('.sitemap__title a').each(function () {
                var id = $(this).attr('href');

                if (y >= $(id).offset().top - 50) {
                    $('.sitemap__title').not(this).removeClass('active');

                    $(this).parents().addClass('active');
                    $('.active').next('.sitemap__subitems').slideDown();
                }
            });


        });


    }]);
