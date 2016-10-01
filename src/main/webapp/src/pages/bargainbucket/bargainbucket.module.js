import angular from 'angular';
import {routing} from './bargainbucket.config.js';

import bargainbucketComponent from './bargainbucket-component/bargainbucket-component';
import template from './bargainbucket-component/bargainbucket-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('bargainbucket', {controller: bargainbucketComponent, template})
  .name;
