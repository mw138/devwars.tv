import angular from 'angular';
import uirouter from 'angular-ui-router';

import ListItemComponent from './list-item-component/list-item-component';
import template from './list-item-component/list-item-component.tpl.html';

export default angular
    .module('main.app')
    .component('listItem', { controller: ListItemComponent, template })
    .name;
