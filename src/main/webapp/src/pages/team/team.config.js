export function routing($urlRouterProvider, $stateProvider) {
  $stateProvider
    .state('team', {
      url: '/team',
      template: '<team></team>'
    })
}
