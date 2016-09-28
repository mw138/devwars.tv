export function routing($urlRouterProvider, $stateProvider) {

  console.log('config url');

  $urlRouterProvider.otherwise('/');

  $stateProvider
    .state('app', {
      url: '/',
      template: '<app-component></app-component>'
    })
}
