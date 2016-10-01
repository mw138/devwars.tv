import angular from 'angular';
import {routing} from './dick-bag.config.js';

import DickBagComponent from './dick-bag-component/dick-bag-component';
import template from './dick-bag-component/dick-bag-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('dickBag', {controller: DickBagComponent, template})
  .name;
