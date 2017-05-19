var Charlatan = require('charlatan'),
    edn = require('jsedn'),
    express = require('express'),
    socket = require('socket.io'),
    app = require('express')(),
    http = require('http').Server(app),
    io = require('socket.io')(http);

var jerseyNumbers = [];
var players = {};
var controllerState = {};

for (var i = 0; i < 100; i++)
  jerseyNumbers.push(i);

shuffle(jerseyNumbers);

// https://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
function shuffle(array) {
  var currentIndex = array.length, temporaryValue, randomIndex;

  // While there remain elements to shuffle...
  while (0 !== currentIndex) {

    // Pick a remaining element...
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;

    // And swap it with the current element.
    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
}

function allocateJerseyNumber() {
  return jerseyNumbers.shift();
}

function freeJerseyNumber(x) {
  console.log("* reclaim #" + x)
  return jerseyNumbers.push(x);
}

function newPlayer(id) {
  players[id] = { name: Charlatan.Name.name(),
                  number: allocateJerseyNumber() }
}

function removePlayer(id) {
  console.log("* remove " + players[id].name + " (" + id + ")");
  freeJerseyNumber(players[id].number);
  delete players[id];
}

app.use(express.static('public'));

app.get('/', function(req, res){
  // res.type("image/svg+xml");
  res.sendFile(__dirname + '/controller.svg');
});

app.get('/players.edn', function(req, res){
  // res.type("application/edn");
  res.send(edn.encode(players));
});

app.get('/controllers.edn', function(req, res){
  // res.type("application/edn");
  res.send(edn.encode(controllerState));
});

io.on('connection', function(socket){
  newPlayer(socket.id);
  console.log("* introduce " + players[socket.id].name + " #" + players[socket.id].number + " (" + socket.id + ")");
  socket.emit('introduce', players[socket.id]);
  socket.on('button-down', function(data) {
    console.log("* button-down", players[socket.id].name, data);
    controllerState[socket.id] = controllerState[socket.id] || {};
    controllerState[socket.id][data] = true;
    console.log(edn.encode(controllerState));
  })
  socket.on('button-up', function(data) {
    console.log("* button-up", players[socket.id].name, data);
    controllerState[socket.id] = controllerState[socket.id] || {};
    delete controllerState[socket.id][data];
  })
  socket.on('disconnect', function() {
    removePlayer(socket.id);
  });
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});