// Styles
import './main.css';

// 3rd party modules
//import bootstrap from 'bootstrap';
import angular from 'angular';
import angularAnimate from 'angular-animate';
import angularUiBootstrap from 'angular-ui-bootstrap';

// Pages
// import home from './pages/home/home.module';
// import profile from './pages/profile/profile.module';

// Modules
import app from './components/app/app.module';
import header from './components/header/header.module';
import ListItem from './components/list-item/list-item.module';

// Team & Profile 
import AccoladeStat from './components/accolade-stat/accolade-stat.module';
import ActivityStat from './components/activity-stat/activity-stat.module';
import FriendsStat from './components/friends-stat/friends-stat.module';
import RankStat from './components/rank-stat/rank-stat.module';
import SocialStat from './components/social-stat/social-stat.module';
import TeamStat from './components/team-stat/team-stat.module';

angular.module('main', [
  angularAnimate, angularUiBootstrap, app, header, ListItem, AccoladeStat, ActivityStat, FriendsStat, RankStat, SocialStat, TeamStat
]);


angular.element(document).ready(() => {
  angular.bootstrap(document, ['main']);
});