import angular from 'angular';
import uirouter from 'angular-ui-router';

import socialStatComponent from './social-stat-component/social-stat-component';
import template from './social-stat-component/social-stat-component.tpl.html';

export default angular
    .module('main.app')
    .component('socialStat', { controller: socialStatComponent, template })
    .name;
