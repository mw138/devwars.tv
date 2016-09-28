import angular from 'angular';
import {routing} from './contact.config.js';

import contactComponent from './contact-component/contact-component';
import template from './contact-component/contact-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('contact', {controller: contactComponent, template})
  .name;
