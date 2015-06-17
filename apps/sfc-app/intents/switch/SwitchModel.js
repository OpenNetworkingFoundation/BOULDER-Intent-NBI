/**
 * Root models using Qooxdoo
 */

/**
 * Define a generic Property class for storing name/value pairs
 */
qx.Class.define("ca.inocybe.ether.Property", {
	extend: qx.core.Object,
	construct: function(name, value) {
		this.initName(name);
		this.initValue(value);
	},
	properties: {
		name: {deferredInit: true},
		value: {deferredInit: true}
	}
});


/** 
 * A generic mixin to add an array of additional properties, which is
 * an array of Property types which can be used to add data to the model
 * that is not a full member.
 */
qx.Mixin.define("ca.inocybe.ether.MProps", {
	construct: function() {
		this.initAdditionalProperties([]);
	},
	properties: {
		additionalProperties: {deferredInit: true, check: "Array"}
	},
	members: {
		
		addProperty: function(property) {
			this.getAdditionalProperties().push(property);
		},
		removeProperty: function (name) {
			for ( var x = 0; x < this.getAdditionalProperties().length; x++) {
				if (this.getAdditionalProperties()[x] == name) {
					this.getAdditionalProperties().splice(x, 1);
				}
			}
		},
		getProperty: function(name) {
			for ( var x = 0; x < this.getAdditionalProperties().length; x++) {
				if (this.getAdditionalProperties()[x] == name) {
					return this.getAdditionalProperties()[x];
				}
			}
		}
	}
});

qx.Class.define("ca.inocybe.ether.Switch", {
	extend: qx.core.Object,
	include: ca.inocybe.ether.MProps,
	construct : function(name) {
		this.initSwName(name);
		this.initPorts([]);
	},
	properties: {
		swName: {deferredInit: true, nullable: true},
		osVersion: {nullable: true},
		ports: {deferredInit: true}
	},
	members : {	
		getPort : function(num) {
			for ( var x = 0; x < this.getPorts().length; x++) {
				if (this.getPorts()[x].getNumber() == num)
					return this.getPorts()[x];
			}
		},
		// Add a port Object to the list. Order the ports in ascending order
		addPort : function(port) {
			// apply validation on the port object
			if (!port instanceof ca.inocybe.ether.Port) {
				log("info", "invalide Port type");
				throw new qx.core.ValidationError(
						"Can only add Port objects to this array");
			}
			for ( var x = 0; x <= this.getPorts().length; x++) {
				if (x == this.getPorts().length) {
					this.getPorts().push(port);
					break;
				}
				if (port.getNumber() > this.getPorts()[x].getNumber())
					continue;
				else if (port.getNumber() == this.getPorts()[x].getNumber()) {
					// Duplicate. Overwrite the existing entry.
					this.getPorts().splice(x, 1, port);
					break;
				}
				else if (port.getNumber() < this.getPorts()[x].getNumber()) {
					this.getPorts().splice(x, 0, port);
					break;
				}
			}
		},
		// Remove a Port from the list
		removePort : function(num) {
			for ( var x = 0; x < this.getPorts().length; x++) {
				if (this.getPorts()[x].getNumber() == num) {
					this.getPorts().splice(x, 1);
				}
			}
		}
	}

});

/**
 * The port class
 * 
 * @returns
 */
qx.Class.define("ca.inocybe.ether.Port", {
	extend: qx.core.Object,
	include: ca.inocybe.ether.MProps,
	construct : function(num) {
		this.initNumber(num);
	},
	properties : {
		// The port number
		number : {deferredInit: true},
		// The bandwidth is Mbps
		capacity : {nullable: true},
		// The port description
		description : {nullable: true},
		// The type of port as it is retrieved from the command output
		type : {nullable: true},
		// The ether specific type constant
		mappedType : {nullable: true},
		// The operational status of the port.
		status : {nullable: true}
	}
});



