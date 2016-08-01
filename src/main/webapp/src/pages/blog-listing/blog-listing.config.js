export function routing($urlRouterProvider, $stateProvider) {
  $stateProvider
    .state('blog-listing', {
      url: '/blog',
      template: '<blog-listing></blog-listing>'
    })
}
