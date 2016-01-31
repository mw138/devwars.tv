var _twitchUsername = 'b3zman41';
var _appServer = 'http://dragon.umryn.net:22050';


(function (angular, undefined) {
    var angularSpectrumColorpicker = angular.module("angularSpectrumColorpicker", []);
    (function (undefined) {
        angularSpectrumColorpicker.directive("spectrumColorpicker", function () {
            return {
                restrict: "E",
                require: "ngModel",
                scope: false,
                replace: true,
                template: '<span><input class="input-small" /></span>',
                link: function ($scope, $element, attrs, $ngModel) {
                    var $input = $element.find("input");
                    var fallbackValue = $scope.$eval(attrs.fallbackValue);
                    var format = $scope.$eval(attrs.format) || undefined;

                    function setViewValue(color) {
                        var value = fallbackValue;
                        if (color) {
                            value = color.toString(format)
                        } else {
                            if (angular.isUndefined(fallbackValue)) {
                                value = color
                            }
                        }
                        $ngModel.$setViewValue(value)
                    }

                    var onChange = function (color) {
                        $scope.$apply(function () {
                            setViewValue(color)
                        })
                    };
                    var onToggle = function () {
                        $input.spectrum("toggle");
                        return false
                    };
                    var options = angular.extend({
                        color: $ngModel.$viewValue,
                        change: onChange,
                        move: onChange,
                        hide: onChange
                    }, $scope.$eval(attrs.options));
                    if (attrs.triggerId) {
                        angular.element(document.body).on("click", "#" + attrs.triggerId, onToggle)
                    }
                    $ngModel.$render = function () {
                        $input.spectrum("set", $ngModel.$viewValue || "")
                    };
                    if (options.color) {
                        $input.spectrum("set", options.color || "");
                        setViewValue(options.color)
                    }
                    $input.spectrum(options);
                    $scope.$on("$destroy", function () {
                        $input.spectrum("destroy")
                    })
                }
            }
        })
    })()
})(window.angular);
(function () {
    angular.module("LivePoll-Client", ["Progress", "Tooltip", "angularSpectrumColorpicker"]).config(function ($interpolateProvider) {
    })
})();
(function () {
    angular.module("LivePoll-Client").controller("LivePollClientCtrl", LivePollClientCtrl);
    LivePollClientCtrl.$inject = ["$rootScope", "$scope", "$timeout", "$interval"];

    function LivePollClientCtrl($rootScope, $scope, $timeout, $interval) {
        $scope.connected = false;
        $scope.throttled = false;
        $scope.user = null;
        $scope.defaultStyle = {
            bgColor: "rgba(0, 0, 0, .75)",
            barColor: "rgb(231, 163, 17)",
            barTextColor: "#f1f1f1",
            barTextShadowColor: "rgba(0, 0, 0, .5)",
            optionBgColor: "rgb(255, 255, 255)",
            optionTextColor: "#333333",
            optionTextShadowColor: "rgba(0, 0, 0, 0)",
            outerTextColor: "#f1f1f1",
            outerTextShadowColor: "rgba(0, 0, 0, .5)"
        };
        $scope.getDefaultStyle = function (styleRule) {
            if (typeof $scope.defaultStyle[styleRule] !== "undefined") {
                return $scope.defaultStyle[styleRule]
            }
        };
        $scope.resetStyles = function () {
            for (var styleRule in $scope.defaultStyle) {
                $scope.input[styleRule] = $scope.defaultStyle[styleRule]
            }
        };
        $scope.question = "";
        $scope.totalVotes = 0;
        $scope.options = [];
        var defaultInput = {
            bgColor: $scope.getDefaultStyle("bgColor"),
            barColor: $scope.getDefaultStyle("barColor"),
            barTextColor: $scope.getDefaultStyle("barTextColor"),
            barTextShadowColor: $scope.getDefaultStyle("barTextShadowColor"),
            optionBgColor: $scope.getDefaultStyle("optionBgColor"),
            optionTextColor: $scope.getDefaultStyle("optionTextColor"),
            optionTextShadowColor: $scope.getDefaultStyle("optionTextShadowColor"),
            outerTextColor: $scope.getDefaultStyle("outerTextColor"),
            outerTextShadowColor: $scope.getDefaultStyle("outerTextShadowColor"),
            options: [{
                text: "",
                keyword: ""
            }, {
                text: "",
                keyword: ""
            }],
            question: "",
            subMult: 1,
            subOnly: "false"
        };
        var originalInput = angular.copy(defaultInput);
        $scope.input = angular.copy(originalInput);
        $scope.updateStatus = function (connected, statusText) {
            $timeout(function () {
                $scope.connected = connected;
                $scope.status = statusText
            })
        };
        $scope.testInterval = null;
        $scope.testForm = function () {
            $timeout(function () {
                $interval.cancel($scope.testInterval);
                $scope.question = "Is this teh urn? (Fake Poll)";
                $scope.totalVotes = 100;
                $scope.options = [{
                    text: "Teh urn",
                    keyword: "urn",
                    count: 50
                }, {
                    text: "Not teh urn",
                    keyword: "noep",
                    count: 50
                }];
                $scope.testInterval = $interval(function () {
                    var randomBinary = Math.floor(Math.random() * 9);
                    (randomBinary > 0) ? $scope.options[1].count += 1 : $scope.options[0].count += 1;
                    $scope.totalVotes += 1
                }, 150)
            })
        };
        $scope.testForm();
        $scope.percentage = function (count) {
            var percent = Number(count / $scope.totalVotes * 100).toFixed(2);
            return (isNaN(percent)) ? 0 : percent
        };
        $scope.closePoll = function () {
            $scope.throttleButtons();
            if (socket === undefined) {
                return
            }
            socket.emit("close_poll", $scope.user)
        };
        $scope.resetForm = function () {
            $scope.input.options.splice(2, $scope.input.options.length - 2);
            $scope.input = angular.copy(originalInput);
            $scope.pollForm.$setPristine()
        };
        $scope.addOption = function () {
            $timeout(function () {
                var numOptions = $scope.input.options.length;
                if (numOptions < 8) {
                    $scope.input.options.push({
                        text: "",
                        keyword: ""
                    })
                }
            })
        };
        $scope.removeOption = function () {
            $timeout(function () {
                var numOptions = $scope.input.options.length;
                var lastIndex = numOptions - 1;
                if (numOptions > 2) {
                    $scope.input.options.splice(lastIndex, 1)
                }
            })
        };
        $scope.submitForm = function (valid) {
            $scope.throttleButtons();
            if (valid && typeof socket !== "undefined") {
                var newPoll = {
                    options: $scope.input.options,
                    question: $scope.input.question,
                    subOnly: $scope.input.subOnly,
                    style: {},
                    token: _twitchToken
                };
                for (var styleRule in $scope.defaultStyle) {
                    if ($scope.defaultStyle.hasOwnProperty(styleRule)) {
                        var defaultStyle = $scope.defaultStyle[styleRule];
                        if (defaultStyle !== $scope.input[styleRule]) {
                            newPoll.style[styleRule] = $scope.input[styleRule]
                        }
                    }
                }
                socket.emit("create_poll", newPoll)
            }
        };
        $scope.throttleButtons = function () {
            $scope.throttled = true;
            $timeout(function () {
                $scope.throttled = false
            }, 2000)
        };
        $scope.incSubMult = function () {
            var value = Number($scope.input.subMult);
            if (value < 99) {
                $scope.input.subMult = value + 1
            }
        };
        $scope.decSubMult = function () {
            var value = Number($scope.input.subMult);
            if (value > 1) {
                $scope.input.subMult = value - 1
            }
        };
        if (typeof io === "undefined") {
            $scope.updateStatus(false, "Failed to load socket connection library... Application may be down. Please try reloading this page.");
            return
        }
        var socket = io(_appServer);
        socket.on("connect", function () {
            $scope.updateStatus(true, "Connected.");
            socket.emit("client_connect", {
                user: _twitchUsername,
                token: _twitchToken
            })
        });
        socket.on("disconnect", function () {
            $scope.updateStatus(false, "Disconnected, attempting to reconnect...")
        });
        socket.on("user", function (user) {
            if (user === null) {
                $scope.updateStatus(false, "Your session data does not match what Twitch is telling me. You should try logging out and logging in again.");
                return
            }
            $timeout(function () {
                $scope.user = user
            })
        });
        socket.on("poll", function (poll) {
            $interval.cancel($scope.testInterval);
            $timeout(function () {
                $scope.question = poll.question;
                $scope.totalVotes = poll.totalVotes;
                $scope.options = poll.options
            })
        });
        socket.on("poll_closed", function () {
            $scope.testForm()
        });
        socket.on("vote", function (id) {
            $timeout(function () {
                $scope.options[id].count += 1;
                $scope.totalVotes += 1
            })
        });
        socket.on("vote_switch", function (voteSwitch) {
            $timeout(function () {
                $scope.options[voteSwitch.from].count -= 1;
                $scope.options[voteSwitch.to].count += 1
            })
        })
    }
})();
(function () {
    angular.module("LivePoll-Display", ["ngAnimate", "Progress"]).config(function ($interpolateProvider) {
    })
})();
(function () {
    angular.module("LivePoll-Display").controller("LivePollDisplayCtrl", LivePollDisplayCtrl);
    LivePollDisplayCtrl.$inject = ["$scope", "$window", "$interval", "$timeout"];

    function LivePollDisplayCtrl($scope, $window, $interval, $timeout) {
        $scope.closed = false;
        $scope.options = [];
        $scope.pollActive = false;
        $scope.question = "";
        $scope.style = {};
        $scope.totalVotes = 0;
        $scope.percentage = function (count) {
            var percent = Number(count / $scope.totalVotes * 100).toFixed(2);
            return (isNaN(percent)) ? 0 : percent
        };
        $scope.addColor = function () {
        };
        $scope.textShadow = function (value) {
            if (typeof value !== "undefined") {
                return "1px 1px 0 " + value
            }
            return {}
        };
        if ($window.innerHeight < 500 || $window.innerWidth < 500) {
            $scope.showWindowEnforcer = true
        }
        if (typeof io === "undefined") {
            return
        }
        var socket = io(_appServer);
        socket.on("connect", function () {
            socket.emit("client_connect", {
                user: _twitchUsername
            })
        });
        socket.on("poll", function (poll) {
            $timeout(function () {
                $scope.style.bgColor = poll.style.bgColor;
                $scope.style.barColor = poll.style.barColor;
                $scope.style.barTextColor = poll.style.barTextColor;
                $scope.style.barTextShadowColor = poll.style.barTextShadowColor;
                $scope.style.optionBgColor = poll.style.optionBgColor;
                $scope.style.optionTextColor = poll.style.optionTextColor;
                $scope.style.optionTextShadowColor = poll.style.optionTextShadowColor;
                $scope.style.outerTextColor = poll.style.outerTextColor;
                $scope.style.outerTextShadowColor = poll.style.outerTextShadowColor;
                $scope.question = poll.question;
                $scope.totalVotes = poll.totalVotes;
                $scope.options = poll.options;
                $scope.pollActive = true
            });
            console.log("poll received");
            console.log(poll)
        });
        socket.on("poll_closed", function () {
            console.log("poll closed");
            $timeout(function () {
                $scope.pollActive = false
            })
        });
        socket.on("vote", function (id) {
            $timeout(function () {
                $scope.options[id].count += 1;
                $scope.totalVotes += 1
            });
            console.log("vote received: %s", id)
        });
        socket.on("vote_switch", function (voteSwitch) {
            $timeout(function () {
                $scope.options[voteSwitch.from].count -= 1;
                $scope.options[voteSwitch.to].count += 1
            });
            console.log("vote switch received")
        })
    }
})();
(function () {
    angular.module("Progress", [])
})();
(function () {
    angular.module("Progress").directive("ngProgress", ngProgress);

    function ngProgress() {
        return {
            link: function (scope, element, attrs) {
                attrs.$observe("percent", function (val) {
                    element.css("width", val + "%")
                })
            }
        }
    }
})();
(function () {
    angular.module("Tooltip", [])
})();
(function () {
    angular.module("Tooltip").directive("ngTooltip", ngTooltip);
    ngTooltip.$inject = ["$document"];

    function ngTooltip($document) {
        return {
            link: function (scope, element, attrs) {
                var value = attrs.ngTooltip;
                var body = angular.element($document[0].body);
                var tooltip = angular.element('<div class="tooltip"></div>');
                tooltip.html(value);
                tooltip.css("display", "none");
                body.append(tooltip);
                element.hover(function () {
                    var epos = element.offset();
                    var ey = epos.top;
                    var ex = epos.left;
                    var ew = element.width();
                    var eh = element.height();
                    tooltip.css("left", ex + ew + "px");
                    tooltip.css("top", (ey + (eh / 2)) - 25 + "px");
                    tooltip.stop().fadeIn(200)
                }, function () {
                    tooltip.stop().fadeOut(200)
                })
            }
        }
    }
})();
