var socket;

function cleanUpSVG() {
  var root = document.querySelector("svg");
  root.removeAttribute("x");
  root.removeAttribute("y");
  root.removeAttribute("width");
  root.removeAttribute("height");
  root.removeAttribute("viewBox");
  root.removeAttribute("enable-background");
  root.setAttribute("style","background:#C9C9C9;-moz-user-select: none; -webkit-user-select: none; -ms-user-select:none; user-select:none;-o-user-select:none;");
  root.setAttribute("unselectable", "on");
  root.setAttribute("onselectstart", "return false;");
  root.setAttribute("onmousedown", "return false;");
}

function positionPads() {
  var padding = 50;
  var windowWidth = window.innerWidth;
  var windowHeight = window.innerHeight;
  
  var rightPad = document.querySelector("#right-pad");
  var leftPad = document.querySelector("#left-pad");
  
  var naturalPadDiameter = parseFloat(rightPad.querySelector("circle").getAttribute("r")) * 2;
  var targetPadDiameter = windowHeight - padding * 2;
  if(targetPadDiameter * 2 + padding * 3 > windowWidth) {
    targetPadDiameter = (windowWidth - padding * 3) / 2;
  }
  var scale = targetPadDiameter / naturalPadDiameter ;
  
  var leftX = padding + (naturalPadDiameter / 2) * scale;
  var rightX = windowWidth - padding - (naturalPadDiameter / 2) * scale;
  var y = (windowHeight / 2);
  
  leftPad.setAttribute("transform", "translate(" + leftX + "," + y + ") scale(" + scale + ")");
  rightPad.setAttribute("transform", "translate(" + rightX + "," + y + ") scale(" + scale + ")");
}

function wireButton(id, downFn, upFn) {
  var node = document.querySelector("#" + id);
  
  var downFnFn = function(e) { downFn(id, e); };
  var upFnFn = function(e) { upFn(id, e); };
  
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
  wireButton("button-a", press, release);
  wireButton("button-b", press, release);
  wireButton("button-x", press, release);
  wireButton("button-y", press, release);
  wireButton("dpad-left", press, release);
  wireButton("dpad-right", press, release);
  wireButton("dpad-up", press, release);
  wireButton("dpad-down", press, release);
  positionPads();
  socket = io();
}

window.onresize = function() {
  positionPads();
}