import angular from 'angular';
import uirouter from 'angular-ui-router';

import footerComponent from './footer-component/footer-component';
import template from './footer-component/footer-component.tpl.html';

export default angular
    .module('main.app')
    .component('footer', { controller: footerComponent, template })
    .name;
