import angular from 'angular';
import uirouter from 'angular-ui-router';

import DummyName from './DummyShort-component/DummyShort-component';
import template from './DummyShort-component/DummyShort-component.tpl.html';

export default angular
    .module('main.app')
    .component('DummyTag', { controller: DummyName, template })
    .name;
