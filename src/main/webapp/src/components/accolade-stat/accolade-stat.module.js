import angular from 'angular';
import uirouter from 'angular-ui-router';

import AccoladeStatComponent from './accolade-stat-component/accolade-stat-component';
import template from './accolade-stat-component/accolade-stat-component.tpl.html';

export default angular
    .module('main.app')
    .component('accoladeStat', { controller: AccoladeStatComponent, template })
    .name;
