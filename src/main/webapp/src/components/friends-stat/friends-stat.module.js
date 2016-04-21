import angular from 'angular';
import uirouter from 'angular-ui-router';

import friendsStatComponent from './friends-stat-component/friends-stat-component';
import template from './friends-stat-component/friends-stat-component.tpl.html';

export default angular
    .module('main.app')
    .component('friendsStat', { controller: friendsStatComponent, template })
    .name;
