
var actions = [
    {"url": "http://blue.devwars.tv/index.html", "id": "htmlBlue"},
    {"url": "http://blue.devwars.tv/style.css", "id": "cssBlue"},
    {"url": "http://blue.devwars.tv/main.js", "id": "jsBlue"},
    {"url": "http://red.devwars.tv/index.html", "id": "htmlRed"},
    {"url": "http://red.devwars.tv/style.css", "id": "cssRed"},
    {"url": "http://red.devwars.tv/main.js", "id": "jsRed"}
];
var reload = false;

updateAll();

setInterval(function () {
    if(reload) {
        updateAll();
    }
}, 10000);

function toggleReload() {
    reload = !reload;
}

function updateAll() {
    for(var i = 0; i < actions.length; i++) {
        var action = actions[i];
        console.log("Getting", action);
        set(action)
    }
}

function set(action) {
    getCode(action.url, function (data) {
        console.log('Updating', action);
        updateElement(action.id, clearText(data));
        hljs.highlightBlock(document.getElementById(action.id));
    });
}

function clearText(data) {
    return data
        .replace(/&/g, '&amp;')
        .replace(/>/g, '&gt;')
        .replace(/</g, '&lt;')
        .replace(/\//g, '&#x2F;')
        .replace(/'/g, '&#39;')
        .replace(/"/g, '&quot;');
}

function updateElement(id, content) {
    document.getElementById(id).innerHTML = content;
}

function getCode(url, cb) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            cb(xmlhttp.responseText);
        }
    };
    xmlhttp.open("GET", url, true);
    xmlhttp.send();
}









// Changing Views

var codeview = [".blue-view__html", ".blue-view__css", ".blue-view__js", ".red-view__html", ".red-view__css", ".red-view__js"];
var blueview = [".blue-view__html", ".blue-view__css", ".blue-view__js"];
var redview = [".red-view__html", ".red-view__css", ".red-view__js"];

var makeInactive = function() {   
    for (var i = 0; i < 6; i++) {
        $(codeview[i]).removeClass("team-active");
        $(codeview[i]).removeClass("single-active-blue");
        $(codeview[i]).removeClass("single-active-red").addClass("inactive");
    }
};

var defaultView = function() {   
    for (var i = 0; i < 6; i++) {
        $(codeview[i]).removeClass("single-active-blue");
        $(codeview[i]).removeClass("single-active-red");
        $(codeview[i]).removeClass("inactive");
    }
};

var teamViewRed = function() {   
    for (var i = 0; i < 6; i++) {
        $(codeview[i]).removeClass("single-active-blue");
        $(codeview[i]).removeClass("single-active-red");
    }

    for (var j = 0; j < 3; j++) {
        $(blueview[j]).addClass("inactive");
    }

    for (var k = 0; k < 3; k++) {
        $(redview[k]).removeClass("inactive");
        $(redview[k]).addClass("team-active");
    }    
};

var teamViewBlue = function() {   
    for (var i = 0; i < 6; i++) {
        $(codeview[i]).removeClass("single-active-blue");
        $(codeview[i]).removeClass("single-active-red");
    }

    for (var j = 0; j < 3; j++) {
        $(blueview[j]).addClass("inactive");
    }

    for (var k = 0; k < 3; k++) {
        $(blueview[k]).removeClass("inactive");
        $(blueview[k]).addClass("team-active");
    }    
};


$(".button").click(function() {
    if ( $(this).hasClass("blue-html") ) {
        makeInactive();
        $(".blue-view__html").removeClass("inactive").addClass("single-active-blue");
    } else if ( $(this).hasClass("blue-css") ) {
        makeInactive();
        $(".blue-view__css").removeClass("inactive").addClass("single-active-blue");
    } else if ( $(this).hasClass("blue-js") ) {
        makeInactive();
        $(".blue-view__js").removeClass("inactive").addClass("single-active-blue");
    } else if ( $(this).hasClass("red-html") ) {
        makeInactive();
        $(".red-view__html").removeClass("inactive").addClass("single-active-red");
    } else if ( $(this).hasClass("red-css") ) {
        makeInactive();
        $(".red-view__css").removeClass("inactive").addClass("single-active-red");
    } else if ( $(this).hasClass("red-js") ) {
        makeInactive();
        $(".red-view__js").removeClass("inactive").addClass("single-active-red");
    } else if ( $(this).hasClass("red-team-view") ) {
        makeInactive();
        teamViewRed();
    } else if ( $(this).hasClass("blue-team-view") ) {
        makeInactive();
        teamViewBlue();
    } else {
        makeInactive();
        defaultView();

    }
});





// Fetch Player Names

var DevWarsAPI = "http://devwars.tv/v1/game/currentgame";

$.getJSON(DevWarsAPI, function (json) {
      
    var teamBlue = json.teams.blue.players;
    var teamRed = json.teams.red.players;
            
    var i;
    for (i = 0; i < teamBlue.length; ++i) {
        if (teamBlue[i].language == "HTML") {
            $("#blue-team__html").text('');
            $("#blue-team__html").append(teamBlue[i].user.username);
        } else if (teamBlue[i].language == "CSS") {
            $("#blue-team__css").text('');
            $("#blue-team__css").append(teamBlue[i].user.username);
        } else if (teamBlue[i].language == "JS") {
            $("#blue-team__js").text('');
            $("#blue-team__js").append(teamBlue[i].user.username);
        }
    }

    var j;
    for (j = 0; j < teamRed.length; ++j) {
        if (teamRed[j].language == "HTML") {
            $("#red-team__html").text('');
            $("#red-team__html").append(teamRed[j].user.username);
        } else if (teamRed[j].language == "CSS") {
            $("#red-team__css").text('');
            $("#red-team__css").append(teamRed[j].user.username);
        } else if (teamRed[j].language == "JS") {
            $("#red-team__js").text('');
            $("#red-team__js").append(teamRed[j].user.username);
        }
    }
});
