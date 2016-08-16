import angular from 'angular';
import {routing} from './leaderboards.config.js';

import leaderboardsComponent from './leaderboards-component/leaderboards-component';
import template from './leaderboards-component/leaderboards-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('leaderboards', {controller: leaderboardsComponent, template})
  .name;
