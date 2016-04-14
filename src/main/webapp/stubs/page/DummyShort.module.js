import angular from 'angular';
import {routing} from './DummyShort.config.js';

import DummyName from './DummyShort-component/DummyShort-component';
import template from './DummyShort-component/DummyShort-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('DummyTag', {controller: DummyName, template})
  .name;
