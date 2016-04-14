var fs = require('fs');
var argv = require('yargs').argv;
var mkdirp = require('mkdirp');

function toHyphenCase(str) {
  var indices = [];
  for (var i = 0; i < str.length; i++) {
    var char = str.charAt(i);
    if (char === char.toUpperCase()) {
      indices.push(i);
    }
  }

  str = str.toLowerCase();
  indices = indices.slice(1);
  indices.forEach(function (value, index, array) {
    str = str.slice(0, value) + '-' + str.slice(value);

    indices.forEach(function (item, theIndex) {
      indices[theIndex] = item + 1;
    });
  });

  return str;
}

function toLowerCamelCase(str) {
  str = str.charAt(0).toLowerCase() + str.slice(1);
  return str;
}

function getArgs() {
  var args = process.argv;
  var returnArgs = {};

  args.forEach(function (arg) {
    if (arg.indexOf('=') > -1) {
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

      newStubName = newStubName.split('/').slice(1).join('/');

      var filePath = out + '/' + newStubName + '/' + newFileName;
      var fileDir = out + '/' + newStubName + '/';

      mkdirp.sync(fileDir);

      fs.writeFileSync(filePath, contents, 'utf-8');
    }
  });
};

var args = getArgs();
var generator = new Generator(args, './stubs');

generator.addHandlerForType('component', (args) => {
  var name = args.name;
  var params = {
    'DummyName': name + 'Component',
    'DummyShort': toHyphenCase(name),
    'DummyTag': toLowerCamelCase(name)
  };

  generator.createFromStub('component', params, './src/components/' + toHyphenCase(name));
});

generator.addHandlerForType('page', (args) => {
  var name = args.name;
  var route = args.route;
  var params = {
    'DummyName': name + 'Component',
    'DummyShort': toHyphenCase(name),
    'DummyTag': toLowerCamelCase(name),
    'DummyRoute': route
  };

  generator.createFromStub('page', params, './src/pages/' + toHyphenCase(name));
});

var type = argv._[0];
generator.generate(type);
