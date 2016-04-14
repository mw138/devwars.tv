// Styles
import './main.css';

// 3rd party modules
//import bootstrap from 'bootstrap';
import angular from 'angular';
import angularAnimate from 'angular-animate';
import angularUiBootstrap from 'angular-ui-bootstrap';

// Modules
import app from './components/app/app.module';
import header from './components/header/header.module';
import ListItem from './components/list-item/list-item.module';

angular.module('main', [
  angularAnimate, angularUiBootstrap, app, header, ListItem
]);


angular.element(document).ready(() => {
  angular.bootstrap(document, ['main']);
});