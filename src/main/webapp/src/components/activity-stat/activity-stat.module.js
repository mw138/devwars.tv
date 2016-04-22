import angular from 'angular';
import uirouter from 'angular-ui-router';

import activityStatComponent from './activity-stat-component/activity-stat-component';
import template from './activity-stat-component/activity-stat-component.tpl.html';

export default angular
    .module('main.app')
    .component('activityStat', { controller: activityStatComponent, template })
    .name;
