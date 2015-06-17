/** 
 * Model for flow based switches
 */

/**
 * Mixin for flow based switches. This mixin provides a flows array and access
 * methods for Flow objects.
 */
qx.Mixin.define("ca.inocybe.ether.MFlows", {
	construct : function() {
		this.initFlows([]);
	},
	properties: {
		flows : {deferredInit: true}
	},
	members : {
		getFlow : function(id) {
			for ( var x = 0; x < this.getFlows().length; x++) {
				if (this.getFlows()[x].getId() == id)
					return this.getFlows()[x];
			}
		},

		// Add a Flow Object to the list.
		addFlow : function(flow) {
			if (!flow instanceof ca.inocybe.ether.Flow) {
				throw new qx.core.ValidationError(
						"Can only add Flow objects to this array");
			}
			
			this.getFlows().push(flow);
		},
		// Remove a Flow from the list
		removeFlow : function(id) {
			for ( var x = 0; x < this.getFlows().length; x++) {
				if (this.getFlows()[x].getId() == id) {
					this.getFlows().splice(x, 1);
				}
			}
		}
	}
});

/**
 * A mixin to add Flow properties to a port
 */
qx.Mixin.define("ca.inocybe.ether.MFlowPort", {
	construct: function() {
		this.initFlows([]);
	},
	properties: {
		//an array of Flow ids that are configured on the port
		flows: {deferredInit: true}
	},
	members: {
		// Add a Flow id to the list. Sort the list is ascending order
		addFlow : function(id) {
			for(var x = 0; x <= this.getFlows().length; x++) {
				if(x == this.getFlows().length) {
					this.getFlows().push(id);
					break;
				}
				if (id > this.getFlows()[x]) 
					continue;
				else if(id == this.getFlows()[x]) {
					//Duplicate. Overwrite the existing entry.
					this.getFlows().splice(x,1,id);
					break;
				}
				else if(id < this.getFlows()[x]) {
					this.getFlows().splice(x,0,id);
					break;
				}
			}
		},
		
		//Remove a Flow from the list
		removeFlow : function(id) {
			for(var x=0; x<this.getFlows().length; x++) {
				if(this.getFlows()[x] == id) {
					this.getFlows().splice(x, 1);
				}
			}
		}
	}
});

/**
 * A Flow switch that extends the base Switch and uses the Flow mixin
 */
qx.Class.define("ca.inocybe.ether.FlowSwitch", {
	extend : ca.inocybe.ether.Switch,
	include : ca.inocybe.ether.MFlows,

	construct : function(name) {
		// call super constructor
		this.base(arguments, name);
	}
});

/**
 * A Port Class extension to represent a FLOW enabled Port
 */
qx.Class.define("ca.inocybe.ether.FlowPort", {
	extend : ca.inocybe.ether.Port,
	include : ca.inocybe.ether.MFlowPort,
	construct : function(num) {
		// call super constructor
		this.base(arguments, num);
	}
});

qx.Class.define("ca.inocybe.ether.Flow", {
	extend: qx.core.Object,
	construct : function() {
		// ID is the current date in milliseconds to ensure uniqueness
		this.initId(new Date().getTime());
		this.initActions([]);
	},
	properties: {
		//the flow ID
		id : {deferredInit: true},
		// input port
		inPort : {nullable: true},
		// incoming flow id (only for tagged flows)
		inVlan : {nullable: true},
		// Array of Property types (name/value pairs) to describe the actions.
		actions : {deferredInit: true},
		// the raw flow string
		rawFlow : {nullable: true},
	},
	members : {		
		addAction : function(action) {
			this.getActions().push(action);
		},
		getAction : function(name) {
			for ( var x = 0; x < this.getActions().length; x++) {
				if (this.getActions()[x].getName() == name)
					return this.getActions()[x].getValue();
			}
		}
	}
});