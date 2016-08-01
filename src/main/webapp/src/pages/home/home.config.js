export function routing($urlRouterProvider, $stateProvider) {
  $stateProvider
    .state('home', {
      url: '/',
      template: '<home></home>'
    })
}
