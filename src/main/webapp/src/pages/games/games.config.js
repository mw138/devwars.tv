export function routing($urlRouterProvider, $stateProvider) {
  $stateProvider
    .state('games', {
      url: '/games',
      template: '<games></games>'
    })
}
