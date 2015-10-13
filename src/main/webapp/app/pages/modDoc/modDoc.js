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

		$('a[href]').click(function() {
				var target = $(this.hash);
				target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
				if (target.length) {
					$('html,body').animate({
						scrollTop: target.offset().top
					}, 1000);
					return false;
				}
		});
    }]);