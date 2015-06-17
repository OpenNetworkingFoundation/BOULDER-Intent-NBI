
/* Some global properties */

/* Bandwidth constants */
var FASTETHERNET_BANDWIDTH = 100;
var GIGABITETHERNET_BANDWIDTH = 1000;
var TENGIGABITETHERNET_BANDWIDTH = 10000;

/* Port type constants */
var FASTETHERNET = "FastEthernet";
var GIGABITETHERNET = "GigabitEthernet";
var TENGIGABITETHERNET = "TenGigabitEthernet";

/**
 * Send a command to the device and return the response
 * @param cmd - the command in string form
 */
function sendCommand(cmd) {
	var remoteShellMessage = {
		remoteShellRequest : {
			command : cmd
		}
	};
	//log("info", "Sending Command: " + cmd);
	var resp = engine.sendMessageBody(remoteShellMessage);
	log("info", "Received Response: " + resp);
	return resp;
}

/**
 * Given a value, return the port capacity constant for that value
 * @param value the bandwidth value from the switch
 */
function getPortCapacity(value) {
	var result = undefined;
	if(value.indexOf("100") != -1 )
		result = FASTETHERNET_BANDWIDTH;
	else if(value.indexOf("a-1G") != -1)
		result = GIGABITETHERNET_BANDWIDTH;
	else if(value.indexOf("10G") != -1)
		result = TENGIGABITETHERNET_BANDWIDTH;
	
	return result;
}

/**
 * Given a value, return the port type constant for that value
 */
function getPortType(value) {
	var result = undefined;
	if(value.indexOf("100") != -1 )
		result = FASTETHERNET;
	else if(value.indexOf("a-1G") != -1)
		result = GIGABITETHERNET;
	else if(value.indexOf("10G") != -1)
		result = TENGIGABITETHERNET;
	
	return result;
}

/**
 * Given a line of output from a device, split it into blocks based on the column sizes
 * @param line the line to split
 * @param columns an array containing the width (in characters) of each column
 * @returns an array containing the line spit into columns
 */
function splitLineIntoBlocks(line, columns) {
	blocks = new Array(columns.length + 1);
	
	try {
		var index = 0;
		for ( var j = 0; j < columns.length; j++) {
			blocks[j] = line.substring(index, index + columns[j]).trim();
			index += columns[j];
		}
		// use the rest of the line to get the final block
		blocks[blocks.length - 1] = line.substring(index).trim();
	} catch (e) {
		return null;
		// Ignore lines that don't match the column sizes.
	}
	return blocks;
}


//inherit() returns a newly created object that inherits properties from the
//prototype object p. It uses the ECMAScript 5 function Object.create() if
//it is defined, and otherwise falls back to an older technique.
function inherit(p) {
	if (p == null)
		throw TypeError(); // p must be a non-null object
	if (Object.create) // If Object.create() is defined...
		return Object.create(p); // then just use it.
	var t = typeof p; // Otherwise do some more type checking
	if (t !== "object" && t !== "function")
		throw TypeError();
	function f() {
	}
	; // Define a dummy constructor function.
	f.prototype = p; // Set its prototype property to p.
	return new f(); // Use f() to create an "heir" of p.
}
