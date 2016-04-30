import angular from 'angular';
import {routing} from './blog-listing.config.js';

import BlogListingComponent from './blog-listing-component/blog-listing-component';
import template from './blog-listing-component/blog-listing-component.tpl.html';

export default angular
  .module('main.app')
  .config(routing)
  .component('blogListing', {controller: BlogListingComponent, template})
  .name;
