export function routing($urlRouterProvider, $stateProvider) {
  $stateProvider
    .state('DummyShort', {
      url: 'DummyRoute',
      template: '<DummyShort></DummyShort>'
    })
}
