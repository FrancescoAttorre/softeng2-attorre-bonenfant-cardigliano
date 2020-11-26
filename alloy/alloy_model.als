//Utilities Signatures
abstract sig Bool {}
one sig True extends Bool {}
one sig False extends Bool {}


//Customers signatures

abstract sig Customer {}

abstract sig AppCustomer extends Customer {}
sig PhysicalCustomer extends Customer {}

sig RegisteredAppCustomer extends AppCustomer {
	username: String,
	password: String //da mettere nei requisiti non funzionali
}
sig UnregisteredAppCustomer extends AppCustomer {}

//---------------------------------------------------------------

//Store Manager signature

sig StoreManager {
	worksIn: one Building
}

//Activity signature

sig Activity {}

//Building signature

sig Building {
	//managedBy: disj some StoreManager,
	ownedBy: one Activity,

	canContain: Int
} {
	canContain > 0
}

fun storeManagers[b: Building]: StoreManager {
	worksIn.b
}

sig Department {
	locatedIn: one Building
}

//-------------------TICKET SIGNATURES-----------------------------


abstract sig DigitalTicket {
	ownedBy: one (AppCustomer + StoreManager),
	requestedByStoreManager: one Bool,
	building: one Building
} {
	ownedBy in StoreManager iff (requestedByStoreManager in True and ownedBy in storeManagers[building])
	
}

sig LineUpDigitalTicket extends DigitalTicket {
	
}




//------------------ FACTS ----------------------------------------

fact usernameIsUnique {
	all rac1, rac2: RegisteredAppCustomer | rac1 != rac2 implies rac1.username != rac2.username
}




//assert a DigitalTicket is owned only by a StoreManager working in the Building the ticket grants access for
assert CoherentSM_Ticket {
	no sm : StoreManager |
		(some b1, b2: Building | 
			(some dt : DigitalTicket | b1 != b2 and b1 = dt.building and sm = dt.ownedBy and b2 = sm.worksIn))
}


run {} for 4 but exactly 3 DigitalTicket
//check CoherentSM_Ticket


assert CoherentSM_Ticket2 {
	no dt: DigitalTicket | dt.ownedBy in storeManagers[dt.building] and dt.requestedByStoreManager in False
}







