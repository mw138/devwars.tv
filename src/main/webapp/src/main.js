// Styles
import './devwars.css';

// 3rd party modules
//import bootstrap from 'bootstrap';
import angular from 'angular';
import angularAnimate from 'angular-animate';
import angularUiBootstrap from 'angular-ui-bootstrap';

// Modules
import app from './components/app/app.module';

angular.module('main', [
  angularAnimate, angularUiBootstrap, app
]);

angular.element(document).ready(() => {
  angular.bootstrap(document, ['main']);
});
