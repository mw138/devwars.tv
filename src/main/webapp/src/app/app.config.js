export function routing($urlRouterProvider, $stateProvider) {

    $stateProvider
        .state('app', {
            url: '/',
            template: '<app-component></app-component>'
        })
}
