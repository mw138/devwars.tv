import angular from 'angular';
import uirouter from 'angular-ui-router';

import rankStatComponent from './rank-stat-component/rank-stat-component';
import template from './rank-stat-component/rank-stat-component.tpl.html';

export default angular
    .module('main.app')
    .component('rankStat', { controller: rankStatComponent, template })
    .name;
