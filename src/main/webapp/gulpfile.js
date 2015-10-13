'use strict';

// Include Gulp & Tools We'll Use
var gulp = require('gulp');
var $ = require('gulp-load-plugins')();
var del = require('del');
var runSequence = require('run-sequence');
var browserSync = require('browser-sync');
var pagespeed = require('psi');
var reload = browserSync.reload;
var concat = require('gulp-concat');
var ngmin = require('gulp-ngmin');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var pipe = require('multipipe');
var url = require('url');
var fs = require('fs');

var AUTOPREFIXER_BROWSERS = [
    'ie >= 10',
    'ie_mob >= 10',
    'ff >= 30',
    'chrome >= 34',
    'safari >= 7',
    'opera >= 23',
    'ios >= 7',
    'android >= 4.4',
    'bb >= 10'
];

// Compile and Automatically Prefix Stylesheets
gulp.task('styles', function () {
    // For best performance, don't add Sass partials to `gulp.src`
    return gulp.src([
        'assets/sass/*.scss',
        'assets/sass/**/*.css'
    ])
        .pipe($.changed('styles', {extension: '.scss'}))
        .pipe($.rubySass({
            style: 'expanded',
            precision: 10
        }))
        .on('error', console.error.bind(console))
        .pipe($.autoprefixer({browsers: AUTOPREFIXER_BROWSERS}))
        .pipe(gulp.dest('.tmp/styles'))
        // Concatenate And Minify Styles
        .pipe($.if('*.css', $.csso()))
        .pipe(gulp.dest('dist/styles'))
        .pipe(gulp.dest('assets/sass'))
        .pipe($.size({title: 'styles'}));
});

gulp.task('dist', function () {
    var assets = $.useref.assets();

    return gulp.src('index.html')
        .pipe(rename('index.min.html'))
        .pipe(assets)
        .pipe($.if('*.js', pipe(ngmin(), uglify())))
        .pipe($.if('*.css', $.csso()))
        .pipe(assets.restore())
        .pipe($.useref())
        .pipe(gulp.dest('./'));
});

// Watch Files For Changes & Reload
gulp.task('serve', ['styles', 'watch-files'], function () {
    browserSync({
        notify: false,
        open: false,
        // Run as an https by uncommenting 'https: true'
        // Note: this uses an unsigned certificate which on first access
        //       will present a certificate warning in the browser.
        // https: true,
        injectChanges: true,
        port:81,

        server: {
            baseDir: "./",

            middleware: [
                function(req, res, next) {
                    var fileName = url.parse(req.url);

                    if(fileName.path.indexOf('.') < 0) req.url = "/index.html";

                    return next();
                }
            ]
        },
    });
});

gulp.task('watch-files', function(){
    gulp.watch(['app/**/*.html'], reload);
    gulp.watch(['app/**/*.js'], reload);
    gulp.watch(['app/**/**/*.js'], reload);
    gulp.watch(['assets/sass/**/*.scss'], ['styles', reload]);
    gulp.watch(['app/images/**/*'], reload);
});

gulp.task('watch', ['styles', 'watch-files']);
