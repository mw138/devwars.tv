import angular from 'angular';
import {routing} from './home.config.js';

import HomeComponent from './home-component/home-component';
import template from './home-component/home-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('home', {controller: HomeComponent, template})
  .name;
