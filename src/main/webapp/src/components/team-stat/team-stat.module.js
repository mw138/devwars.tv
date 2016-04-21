import angular from 'angular';
import uirouter from 'angular-ui-router';

import teamStatComponent from './team-stat-component/team-stat-component';
import template from './team-stat-component/team-stat-component.tpl.html';

export default angular
    .module('main.app')
    .component('teamStat', { controller: teamStatComponent, template })
    .name;
