import angular from 'angular';
import {routing} from './team.config.js';

import TeamComponent from './team-component/team-component';
import template from './team-component/team-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('team', {controller: TeamComponent, template})
  .name;
