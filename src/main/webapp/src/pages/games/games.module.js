import angular from 'angular';
import {routing} from './games.config.js';

import gamesComponent from './games-component/games-component';
import template from './games-component/games-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('games', {controller: gamesComponent, template})
  .name;
