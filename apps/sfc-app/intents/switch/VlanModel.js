/**
 * Models for Vlan based devices
 */

/**
 * Mixin to add VLAN properties to a switch. This will add a vlans array and
 * access functions to the class mixing it in.
 */
qx.Mixin.define("ca.inocybe.ether.MVlans", {
	construct : function() {
		this.initVlans([]);
	},
	properties: {
		vlans: {deferredInit: true}
	},
	members : {
		getVlan : function(id) {
			for ( var x = 0; x < this.getVlans().length; x++) {
				if (this.getVlans()[x].id == id)
					return this.getVlans()[x];
			}
		},

		// Add a Vlan Object to the list. Sort Vlans in assending order
		addVlan : function(vlan) {
			if (!vlan instanceof ca.inocybe.ether.Vlan) {
				throw new qx.core.ValidationError(
						"Can only add Vlan objects to this array");
			}
			for ( var x = 0; x <= this.getVlans().length; x++) {
				if (x == this.getVlans().length) {
					this.getVlans().push(vlan);
					break;
				}
				if (vlan.getId() > this.getVlans()[x].getId())
					continue;
				else if (vlan.getId() == this.getVlans()[x].getId()) {
					// Duplicate. Overwrite the existing entry.
					this.getVlans().splice(x, 1, vlan);
					break;
				}
				else if (vlan.getId() < this.getVlans()[x].getId()) {
					this.getVlans().splice(x, 0, vlan);
					break;
				}
			}
		},
		// Remove a Vlan from the list
		removeVlan : function(id) {
			for ( var x = 0; x < this.getVlans().length; x++) {
				if (this.getVlans()[x].getId() == id) {
					this.getVlans().splice(x, 1);
				}
			}
		}
	}

});

/**
 * A mixin to add VLAN properties to a port
 */
qx.Mixin.define("ca.inocybe.ether.MVlanPort", {
	construct : function() {
		this.initVlans([]);
	},
	statics : {
		// port "mode" values
		ACCESS : "access",
		TRUNK : "trunk",
		DOT1QTUNNEL : "dot1q-tunnel"
	},
	properties: {
		vlans: {deferredInit: true},
		// port modes. can be "access", "trunk" or "dot1q-tunnel"
		mode: {init: "access"}
	},
	members : {
		// Add a VLAN id to the list. Sort the list is ascending order
		addVlan : function(id) {
			for ( var x = 0; x <= this.getVlans().length; x++) {
				if (x == this.getVlans().length) {
					this.getVlans().push(id);
					break;
				}
				if (id > this.getVlans()[x])
					continue;
				else if (id == this.getVlans()[x]) {
					// Duplicate. Overwrite the existing entry.
					this.getVlans().splice(x, 1, id);
					break;
				}
				else if (id < this.getVlans()[x]) {
					this.getVlans().splice(x, 0, id);
					break;
				}
			}
		},

		// Remove a Vlan from the list
		removeVlan : function(id) {
			for ( var x = 0; x < this.getVlans().length; x++) {
				if (this.getVlans()[x] == id) {
					this.getVlans().splice(x, 1);
				}
			}
		}
	}
});

/**
 * A VLAN Switch. This class extends the Switch class and uses the VLAN Mixin to
 * provide basic VLAN management.
 */
qx.Class.define("ca.inocybe.ether.VlanSwitch", {
	extend : ca.inocybe.ether.Switch,
	include : ca.inocybe.ether.MVlans,
	construct : function(name) {
		// call super constructor
		this.base(arguments, name);
	}
});

/**
 * A Port Class extension to represent a VLAN enabled Port
 */
qx.Class.define("ca.inocybe.ether.VlanPort", {
	extend : ca.inocybe.ether.Port,
	include : ca.inocybe.ether.MVlanPort,
	construct : function(num) {
		// call super constructor
		this.base(arguments, num);
	}
});

/**
 * Definition for a VLAN
 */
qx.Class.define("ca.inocybe.ether.Vlan", {
	extend : qx.core.Object,
	include : ca.inocybe.ether.MProps,
	construct : function(num) {
		this.initId(num);
		this.initPorts(new Array());
	},
	properties: {
		id : {deferredInit: true},
		description : {nullable: true},
		status :  {nullable: true},
		// List of port numbers that are on this VLAN
		ports :  {deferredInit: true},
	},
	members : {
		// Add a port number to the vlan's port list. The list is in
		// assending order
		addPort : function(num) {
			
			for ( var x = 0; x <= this.getPorts().length; x++) {
				if (x == this.getPorts().length) {
					this.getPorts().push(num);
					break;
				}
				if (num > this.getPorts()[x])
					continue;
				else if (num == this.getPorts()[x]) {
					// Duplicate. Overwrite the existing entry.
					this.getPorts().splice(x, 1, num);
					break;
				}
				else if (num < this.getPorts()[x]) {
					this.getPorts().splice(x, 0, num);
					break;
				}
			}
		},
		// remove a port number
		removePort : function(num) {
			for ( var x = 0; x < this.getPorts().length; x++) {
				if (this.getPorts()[x] == num) {
					this.getPorts().splice(x, 1);
				}
			}
		}
	}
});