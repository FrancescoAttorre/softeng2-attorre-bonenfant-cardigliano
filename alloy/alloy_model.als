//Utilities Signatures
open util/time
abstract sig Bool {}
one sig True extends Bool {}
one sig False extends Bool {}



//Customers signatures

abstract sig Customer {}

abstract sig AppCustomer extends Customer {}
sig PhysicalCustomer extends Customer {}

sig RegisteredAppCustomer extends AppCustomer {
	
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
	ownedBy: Activity,
	closed: dynamic[Bool],
	baseCapacity: Int,
	contains: dynamicSet[Customer]

} {
	baseCapacity > 0
}


sig Department {
	locatedIn: one Building,
	surplusCapacity: Int,
	contains: dynamicSet[RegisteredAppCustomer]
} {
	surplusCapacity >= 0 and #(contains.Time) < surplusCapacity
}

//-------------------TICKET SIGNATURES-----------------------------


abstract sig State {}
one sig Invalid, Valid, Expired extends State {}

abstract sig DigitalTicket {
	state: dynamic[State],
	ownedBy: one (AppCustomer + StoreManager),
	requestedByStoreManager: one Bool,
	building: one Building
} {
	(requestedByStoreManager in True iff ownedBy in storeManagers[building]) and
	(requestedByStoreManager in True implies one pt : PhysicalTicket | this = pt.associatedWith)
	
}

sig LineUpDigitalTicket extends DigitalTicket {
	
} {
	requestedByStoreManager in True
}

sig TimeSlot {
	start: Time,
	end: Time
} {
	start in prevs[end]
}

sig BookingDigitalTicket extends DigitalTicket {
	timeslot: TimeSlot,
	departments: set Department
	
} {
	requestedByStoreManager in False and ownedBy in RegisteredAppCustomer
}


sig PhysicalTicket {
	ownedBy: one PhysicalCustomer,
	associatedWith: one LineUpDigitalTicket
} {
	associatedWith.requestedByStoreManager in True
}



//----------------------FUNCTIONS-----------------------


fun storeManagers[b: Building]: StoreManager {
	worksIn.b
}

fun Building.customers[t: Time]: Customer {
	this.contains.t
}


fun AppCustomer.tickets[]: DigitalTicket {
	ownedBy.this
}

fun PhysicalCustomer.tickets[]: PhysicalTicket {
	ownedBy.this
}

fun AppCustomer.tickets[b: Building]: DigitalTicket {
	//building: DigitalTicket -> Building
	this.tickets[] & building.b
}



//------------------ FACTS ----------------------------------------

fact everyBuildingHasOneSM {
	all b : Building | some sm : StoreManager | b = sm.worksIn
}

fact CustomerHasOneTicketForEachBuilding {
	all ac : AppCustomer | #ac.tickets[] = #(ac.tickets[].building)
}

fact NoCustomerInsideWhenClosed {
	all t : Time | all b : Building | b.closed.t in True implies #(b.contains.t) = 0
}	

 
fact PhysicalTicketAssociatedToOnlyOneDT {
	all disj pt1, pt2 : PhysicalTicket | pt1.associatedWith != pt2.associatedWith
}

fact BuildingContainsCustomersWhoHadValidTicket {
	all t : Time | all b: Building | all ac: AppCustomer | ac in b.contains.t => 
		some t1 : Time | lte[t1, t] and one dt: DigitalTicket | (
			ac = dt.ownedBy and
			dt.state.t1 in Valid)

	//TODO: caso per i physical customers
}

fact CustomerIsInOneBuildingAtATime {
	no t : Time | some disj b1, b2 : Building | #(b1.contains.t & b2.contains.t) > 0
}


fact Trace {
	all t: Time - time/first |
		(one ac : AppCustomer | some b : Building | ac.enters[b,t]) or
		(one pc : PhysicalCustomer | some b: Building | pc.enters[b,t])
}


//------------------PREDICATES----------------------------------------

pred AppCustomer.enters[b: Building, t: Time] {
	--precondition
	this.notInBuilding[prev[t]]			--AppCustomer was not in a building before entering
	this.hasValidDigitalTicket[b, prev[t]]		--Before entering his ticket was valid

	--postcondition
	this in b.customers[t]				--AppCustomer is in a Building
	

}

pred PhysicalCustomer.enters[b: Building, t: Time] {
	--precondition
	this.notInBuilding[prev[t]]
	this.hasValidPhysicalTicket[b, prev[t]]
	

	--postcondition
	this in b.customers[t]
}


pred Customer.notInBuilding[t: Time] {
	this not in Building.customers[t]
}

pred AppCustomer.hasValidDigitalTicket[b: Building, t:Time] {
	this.tickets[b].state.t = Valid
}

pred PhysicalCustomer.hasValidPhysicalTicket[b: Building, t: Time] {
	one dt : (this.tickets[]).associatedWith & ownedBy.(storeManagers[b]) | dt.state.t = Valid
}


//TODO check numero di biglietti per ogni building


run {
	
} for 2 but exactly 1 AppCustomer









