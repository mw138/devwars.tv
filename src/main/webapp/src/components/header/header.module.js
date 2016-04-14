import angular from 'angular';
import uirouter from 'angular-ui-router';

import headerComponent from './header-component/header-component';
import template from './header-component/header-component.tpl.html';

export default angular
    .module('main.app')
    .component('header', { controller: headerComponent, template })
    .name;
