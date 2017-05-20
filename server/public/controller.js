var socket;

function cleanUpSVG() {
  var root = document.querySelector("svg");
  root.removeAttribute("x");
  root.removeAttribute("y");
  root.removeAttribute("width");
  root.removeAttribute("height");
  root.removeAttribute("viewBox");
  root.removeAttribute("enable-background");
  root.setAttribute("unselectable", "on");
  root.setAttribute("onselectstart", "return false;");
  root.setAttribute("onmousedown", "return false;");
  root.setAttribute("style","-moz-user-select: none; -webkit-user-select: none; -ms-user-select:none; user-select:none;-o-user-select:none;");
}

function setBackgroundColor(color) {
  var background = document.querySelector("#background");
  background.setAttribute("fill", color);
  background.setAttribute("width", window.innerWidth);
  background.setAttribute("height", window.innerHeight);
}

function positionText() {
  var nameText = document.querySelector("#name");
  var numberText = document.querySelector("#number");
  var x = window.innerWidth / 2;
  var y1 = 150;
  var y2 = 400;
  
  nameText.setAttribute("transform", "translate(" + x + "," + y1 + ")");
  numberText.setAttribute("transform", "translate(" + x + "," + y2 + ")");
  nameText.setAttribute("text-anchor", "middle");
  numberText.setAttribute("text-anchor", "middle");
}

function positionPads() {
  var padding = 20;
  var infoSpace = 500;
  var windowWidth = window.innerWidth;
  var windowHeight = window.innerHeight;
  
  // var rightPad = document.querySelector("#right-pad");
  var leftPad = document.querySelector("#left-pad");
  
  var naturalPadDiameter = parseFloat(leftPad.querySelector("circle").getAttribute("r")) * 2;
  var targetPadDiameter = windowHeight - padding * 2 - infoSpace;
  if(targetPadDiameter + padding * 2 > windowWidth) {
    targetPadDiameter = windowWidth - padding * 2;
  }
  var scale = targetPadDiameter / naturalPadDiameter;
  
  var leftX = windowWidth / 2;
  var y = windowHeight - padding - targetPadDiameter / 2;
  
  leftPad.setAttribute("transform", "translate(" + leftX + "," + y + ") scale(" + scale + ")");
  // rightPad.setAttribute("transform", "translate(" + rightX + "," + y + ") scale(" + scale + ")");
}

function preventDefault(e) {
  e.preventDefault();
}

function wireButton(id, downFn, upFn) {
  var node = document.querySelector("#" + id);
  
  var downFnFn = function(e) { e.preventDefault(); downFn(id, e); };
  var upFnFn = function(e) { e.preventDefault(); upFn(id, e); };
  
  // node.addEventListener("click", downFnFn, false);
  // node.addEventListener("mousedown", downFnFn, false);
  node.addEventListener("touchstart", downFnFn, false);
  
  // node.addEventListener("mouseup", upFnFn, false);
  node.addEventListener("touchend", upFnFn, false);
  node.addEventListener("touchleave", upFnFn, false);
  node.addEventListener("touchcancel", upFnFn, false);
}

function press(id, e) {
  console.log(id + " pressed", e);
  socket.emit("button-down", id);
}

function release(id, e) {
  console.log(id + " released", e);
  socket.emit("button-up", id);
}

window.onload = function() {
  cleanUpSVG();
  document.rootElement.addEventListener("touchstart", preventDefault);
  document.rootElement.addEventListener("touchend", preventDefault);
  document.rootElement.addEventListener("touchmove", preventDefault);
  wireButton("dpad-left", press, release);
  wireButton("dpad-right", press, release);
  wireButton("dpad-up", press, release);
  wireButton("dpad-down", press, release);
  positionPads();
  positionText();
  setBackgroundColor("black");
  socket = io();
  socket.on('introduce', function(player) {
    document.querySelector("#name").innerHTML = player.name;
    document.querySelector("#number").innerHTML = "#" + player.number;
    setBackgroundColor(player.team == "Green" ? "#00FF00" : "#FF00FF");
    window.onresize();
  })
}

window.onresize = function() {
  positionText();
  positionPads();
}