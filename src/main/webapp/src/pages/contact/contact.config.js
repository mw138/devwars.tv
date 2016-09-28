export function routing($urlRouterProvider, $stateProvider) {
  $stateProvider
    .state('contact', {
      url: '/contact',
      template: '<contact></contact>'
    })
}
