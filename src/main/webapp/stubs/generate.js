var fs = require('fs');
var argv = require('yargs').argv;
var mkdirp = require('mkdirp');

function toTitleCase(str)
{
    return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
}

function toHypenCase(str) {
    var indices = [];
    for(var i = 0; i < str.length; i++) {
        var char = str.charAt(i);
        if(char === char.toUpperCase()) {
            indices.push(i);
        }
    }

    str = str.toLowerCase();
    indices = indices.slice(1);
    indices.forEach(function(value, index, array) {
        str = str.slice(0, value) + '-' + str.slice(value);

        indices.forEach(function(item, theIndex) {
            indices[theIndex] = item + 1;
        });
    });

    return str;
}

function toLowerCamelCase(str) {
    str = toTitleCase(str);
    str = str.charAt(0).toLowerCase() + str.slice(1);
    return str;
}

function getArgs() {
    var args = process.argv;
    var returnArgs = {};

    args.forEach(function(arg) {
        if(arg.indexOf('=') > -1) {
            var left = arg.split('=')[0];
            var right = arg.split('=')[1];

            returnArgs[left] = right;
        }
    });

    return returnArgs;
}

function Generator(args, stubPath) {
    var self = this;

    self.stubPath = stubPath;
    self.handlerMap = {};

    self.args = args;
}

Generator.prototype.addHandlerForType = function (type, handler) {
    this.handlerMap[type] = handler;
};

Generator.prototype.generate = function (type) {
    if (this.handlerMap[type]) {
        this.handlerMap[type](this.args);
    } else {
        console.error("Type Not Handled");
    }
};

Generator.prototype.pathToStub = function (stub) {
    return this.stubPath + '/' + stub;
};

Generator.prototype.createFromStub = function (stub, params, out) {
    var self = this;
    var dir = fs.readdirSync(self.pathToStub(stub));

    dir.forEach(function (file) {
        var stats = fs.lstatSync(self.pathToStub(stub) + '/' + file);

        if (stats.isDirectory()) {
            self.createFromStub(stub + '/' + file, params, out);
        } else {
            var contents = fs.readFileSync(self.pathToStub(stub) + '/' + file, "utf-8");

            var newFileName = file.slice(0);
            var newStubName = stub.slice(0);

            for (var key in params) {
                var val = params[key];

                contents = contents.replace(new RegExp(key, "g"), val);
                newFileName = newFileName.replace(new RegExp(key, "g"), val);
                newStubName = newStubName.replace(new RegExp(key, "g"), val);
            }

            var filePath = out + '/' + newStubName + '/' + newFileName;
            var fileDir = out + '/' + newStubName + '/';

            try {
                mkdirp.sync(fileDir);
            } catch (e) {}

            fs.writeFileSync(filePath, contents, 'utf-8');
        }
    });
};

var args = getArgs();
var generator = new Generator(args, './stubs');

generator.addHandlerForType('component', function (args) {
    var name = args.name;
    name = toTitleCase(name);
    var params = {
        'DummyName': name + 'Component',
        'DummyShort': toHypenCase(name),
        'DummyTag': toLowerCamelCase(name)
    };

    generator.createFromStub('component', params, './src/components/' + name);
});

var type = argv._[0];
generator.generate(type);
