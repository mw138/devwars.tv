export function routing($urlRouterProvider, $stateProvider) {
  $stateProvider
    .state('profile', {
      url: '/profile',
      template: '<profile></profile>'
    })
}
