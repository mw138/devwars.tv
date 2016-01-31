var gulp = require('gulp');
var browserSync = require('browser-sync');
var rename = require('gulp-rename');
var sourcemaps = require('gulp-sourcemaps');
var postcss = require('gulp-postcss');
var sass = require('gulp-sass');
var ngAnnotate = require('gulp-ng-annotate');
var uglify = require('gulp-uglify');
var pipe = require('multipipe');
var useref = require('gulp-useref');
var $ = require('gulp-load-plugins')();
var url = require('url');
var reload = browserSync.reload;

var plugins = [require('precss'), require('autoprefixer'), require('cssnano'), require('postcss-grid')];

gulp.task('styles', function () {
    gulp.src('./assets/sass/main.scss')
        .pipe(sourcemaps.init())
        .pipe(sass().on('error', sass.logError))
        .pipe(postcss(plugins))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('assets/sass/'));
});

gulp.task('dist', function () {
    return gulp.src('index.html')
        .pipe(rename('index.min.html'))
        .pipe(useref())
        .pipe($.if('*.js', pipe(ngAnnotate(), uglify())))
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
        }
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
