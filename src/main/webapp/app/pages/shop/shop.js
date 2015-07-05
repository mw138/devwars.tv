/**
 * Created by Beau on 16 May 2015.
 */
angular.module("app.shop", [])
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('shop', {
                    url: '/shop',
                    templateUrl: '/app/pages/shop/shopView.html',
                    controller: "ShopController"
                });

        }])
    .controller("ShopController", ["$scope", function ($scope) {
        
    }]);