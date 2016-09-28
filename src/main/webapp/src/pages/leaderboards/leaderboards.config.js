export function routing($urlRouterProvider, $stateProvider) {
  $stateProvider
    .state('leaderboards', {
      url: '/leaderboards',
      template: '<leaderboards></leaderboards>'
    })
}
