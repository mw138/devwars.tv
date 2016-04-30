import angular from 'angular';
import uirouter from 'angular-ui-router';

import ScoreStatComponent from './score-stat-component/score-stat-component';
import template from './score-stat-component/score-stat-component.tpl.html';

export default angular
    .module('main.app')
    .component('scoreStat', { controller: ScoreStatComponent, template })
    .name;
