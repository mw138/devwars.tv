import angular from 'angular';
import {routing} from './profile.config.js';

import ProfileComponent from './profile-component/profile-component';
import template from './profile-component/profile-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('profile', {controller: ProfileComponent, template})
  .name;
