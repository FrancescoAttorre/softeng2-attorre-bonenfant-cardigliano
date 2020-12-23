//Utilities Signatures
open util/time
open util/sequniv

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
	contains: dynamicSet[Customer],
	hasQueue: one Queue

} {
	baseCapacity > 0 and all t: Time | #(noDepCustomers[t]) <= baseCapacity
	--(one q: Queue | this = q.relatedTo)
}


sig Department {
	locatedIn: one Building,
	surplusCapacity: Int,
	contains: dynamicSet[RegisteredAppCustomer]
} {
	surplusCapacity >= 0 and all rac: RegisteredAppCustomer, t : Time | (rac in contains.t iff rac in locatedIn.contains.t) and #(contains.t) <= surplusCapacity
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
	all t : Time | state.t = Invalid implies (one q: Queue | this in t.(q.contains).elems)
}

sig TimeSlot {
	start: Time,
	end: Time
} {
	start in prevs[end]
}

sig BookingDigitalTicket extends DigitalTicket {
	timeSlot: one TimeSlot,
	departments: set Department
	
} 
{
	requestedByStoreManager in False and ownedBy in RegisteredAppCustomer and 
	(#departments > 0 implies departments.locatedIn = building)
}


sig PhysicalTicket {
	ownedBy: one PhysicalCustomer,
	associatedWith: one LineUpDigitalTicket
} {
	associatedWith.requestedByStoreManager in True
}

sig Queue {
	contains: Time -> seq/Int -> lone LineUpDigitalTicket,

} {
	(all t: Time | not t.contains.hasDups and all ld: LineUpDigitalTicket | one b : Building | 
		this = b.hasQueue and (ld in t.contains.elems implies ld.state.t = Invalid and ld.building = b))

	and

	(all t: Time | no idx: seq/Int | #(t.contains[idx]) = 0 and (some idx': seq/Int | gt[idx', idx] and #(t.contains[idx']) > 0))
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
	this.tickets[] & building.b
}

fun PhysicalCustomer.tickets[b: Building]: DigitalTicket {
	(this.tickets[]).associatedWith & ownedBy.(storeManagers[b])
}

fun Building.noDepCustomers[t: Time]: Customer {
	this.contains.t - (locatedIn.this).contains.t
}

fun TimeSlot.timeInterval[]: Time {
	{t: Time | gte[t, this.start] and lte[t, this.end]}
}

//Union of Valid Tickets and Tickets associated to Customers that entered. (not including department specific tickets)
fun numberOfValidOrInside[b: Building, t : Time]: Int {
	#({lt: LineUpDigitalTicket | lt.state.t = Valid and lt.building = b} + {lt : LineUpDigitalTicket | one ac : AppCustomer | lt in ac.tickets[b] and ac in b.noDepCustomers[t] }
		+ {lt: LineUpDigitalTicket | one pc : PhysicalCustomer | lt in pc.tickets[b] and pc in b.noDepCustomers[t]
		+ {bdt: BookingDigitalTicket | bdt.state.t = Valid and not bdt.isDepartmentSpecific}} )
}


//------------------ FACTS ----------------------------------------

fact everyBuildingHasOneSM {
	all b : Building | some sm : StoreManager | b = sm.worksIn
}

fact CustomerHasAtMostOneTicketForEachBuilding {
	all ac : AppCustomer | #ac.tickets[] = #(ac.tickets[].building)
}

fact NoCustomerInsideWhenClosed {
	all t : Time | all b : Building | b.closed.t in True implies #(b.contains.t) = 0
}	

 
fact PhysicalTicketAssociatedToOnlyOneDT {
	all disj pt1, pt2 : PhysicalTicket | pt1.associatedWith != pt2.associatedWith
}

//A Customer cannot be inside a Building without entering it.
fact BuildingContainsCustomersWhoEntered {
	(all t : Time, b: Building, ac: AppCustomer | (ac in b.contains.t iff (one t1 : Time | (lte[t1, t] and ac.enters[b, t1] and 
	no t2: Time | gt[t2, t1] and lte[t2,t] and ac.exits[b, t2]))))
	and
	(all t : Time, b: Building, pc: PhysicalCustomer | (pc in b.contains.t iff (one t1 : Time | (lte[t1, t] and pc.enters[b, t1] and 
	no t2: Time | gt[t2, t] and lte[t2,t] and pc.exits[b, t2]))))
}


fact CustomersExitsOnlyThroughExit {
	all t, t1: Time, ac: AppCustomer, b:Building | ((ac in b.customers[t] and gt[t1,t] and 
	not ac in b.customers[t1]) implies some t2: Time | lte[t2,t1] and ac.exits[b,t2])
}

fact CustomersExitsOnlyThroughExit {
	all t, t1: Time, pc: PhysicalCustomer, b:Building | ((pc in b.customers[t] and gt[t1,t] and 
	not pc in b.customers[t1]) implies some t2: Time | lte[t2,t1] and pc.exits[b,t2])
}

//Describe when BookingDigitalTickets become Valid
fact BookingDigitalTicketValidity {
	all t: Time, bt : BookingDigitalTicket | bt.state.t = Valid iff t in (prev[bt.timeSlot.start] + (bt.timeSlot).timeInterval[])
}

//Describe how Tickets become Valid exiting the Queue
fact LineUpDigitalTicketValidity {
	all t: Time - time/first, b: Building | #(b.hasQueue.contains[prev[t]]) > 0 and sub[b.baseCapacity, numberOfValidOrInside[b,prev[t]]] > 0 implies
		b.hasQueue.contains[t] = b.hasQueue.contains[prev[t]].delete[0] else b.hasQueue.contains[t] = b.hasQueue.contains[prev[t]]
		
}


fact CustomerIsInOneBuildingAtATime {
	no t : Time | some disj b1, b2 : Building | #(b1.contains.t & b2.contains.t) > 0
}

fact DigitalTicketStateMachine {
	all t: Time - time/first, dt : DigitalTicket | (dt.state.t = Expired implies (dt.state.(prev[t]) in Valid + dt.state.t)) and
							(dt.state.t = Valid implies (dt.state.(prev[t]) in Invalid + dt.state.t)) and
							(dt.state.t = Invalid implies dt.state.(prev[t]) = dt.state.t)
}


fact No2PhysicalCustomerWith2Tickets {
	no disj pt1, pt2 : PhysicalTicket, pc: PhysicalCustomer  | pt1.ownedBy = pc and pt2.ownedBy = pc
}


fact ValidTicketsDontExceedCapacity {
	all b: Building, t: Time | numberOfValidOrInside[b,t] < b.baseCapacity
}


//Describes the evolution of the dynamic model
fact Trace {
	(all t: Time - time/first | some b: Building |
		 (one ac : AppCustomer | ac.enters[b,t] or ac.exits[b,t]) or
		 (one pc : PhysicalCustomer | pc.enters[b,t] or pc.exits[b,t]) or
		 (b.hasQueue.contains[t] = b.hasQueue.contains[prev[t]].delete[0]) and #b.hasQueue.contains[prev[t]] > 0)

}


//------------------PREDICATES----------------------------------------



pred AppCustomer.enters[b: Building, t: Time] {
	--precondition
	not this.inBuilding[prev[t]]			--AppCustomer was not in a building before entering
	this.hasValidDigitalTicket[b, prev[t]]	--Before entering his ticket was valid

	--postcondition
	this in b.customers[t]				--AppCustomer is in the Building
	
	let ticket = this.tickets[b] |
		ticket in BookingDigitalTicket implies (this.exits[b, next[ticket.timeSlot.end]] and 
									(all t' : (ticket.timeSlot).timeInterval[] | ticket.isDepartmentSpecific
									implies 
 										(all d : ticket.departments | 
											this in d.contains.t' and 
											no d': locatedIn.b - ticket.departments | this in d'.contains.t')
									else
										this in b.noDepCustomers[t']
									)
								    )
									

		else this in b.noDepCustomers[t]
}

pred AppCustomer.exits[b: Building, t: Time] {
	--precondition
	this.inBuilding[prev[t]]
	this in b.customers[prev[t]]
	
	--postcondition
	not this in b.customers[t]
	not this.inBuilding[t]
	this.tickets[b].state.t = Expired
}

pred PhysicalCustomer.exits[b: Building, t: Time] {
	--precondition
	this.inBuilding[prev[t]]
	this in b.customers[prev[t]]

	--postcondition
	not this in b.customers[t]
	not this.inBuilding[t]
	this.tickets[b].state.t = Expired
}



pred BookingDigitalTicket.isDepartmentSpecific {
	#(this.departments) > 0				--Departments are specified
}

pred PhysicalCustomer.enters[b: Building, t: Time] {
	--precondition
	not this.inBuilding[prev[t]]
	this.hasValidPhysicalTicket[b, prev[t]]
	

	--postcondition
	this in b.customers[t] and this in b.noDepCustomers[t]
}


pred Customer.inBuilding[t: Time] {
	this in Building.customers[t]
}

pred AppCustomer.hasValidDigitalTicket[b: Building, t:Time] {
	#(this.tickets[b]) > 0
	this.tickets[b].state.t = Valid
}

pred PhysicalCustomer.hasValidPhysicalTicket[b: Building, t: Time] {
	one dt : this.tickets[b] | dt.state.t = Valid
}

assert RAC_doesNotVisitOtherDepartments {
	no t: Time, rac : RegisteredAppCustomer, d : Department | rac in (d.locatedIn).contains.t 
	and rac not in d.contains.t and rac in d.contains.(next[t])
}

assert NotEnters2Times {
	no t1, t2:Time, ac: AppCustomer, b: Building | gte [t2,t1] and ac in b.contains.t1 and not ac in b.contains.t2 
	and (ac.tickets[b]).state.t2 = Valid
}

assert NoDigitalTicketForSameBuilginAndPerson {
	(no b : Building, ac : AppCustomer | #(ac.tickets[b]) > 1) and
	(no b : Building, pc : PhysicalCustomer | #(pc.tickets[b]) > 1)
	
}

assert NoInvalidTicketOutsideQueue {
	no t: Time, ld: LineUpDigitalTicket, b : Building | ld.state.t = Invalid and ld.building = b and ld not in t.((b.hasQueue).contains).elems
}

assert NoCustomersInsideWhitoutEntering {
	(no t: Time, b: Building, dt: DigitalTicket, pc: PhysicalCustomer | dt in pc.tickets[b] and
		some t1: prevs[t] | pc not in b.contains.t1 and dt.state.t1 = Expired and pc in b.contains.t)


	and

	(no t: Time, b: Building, dt: DigitalTicket, ac: AppCustomer | dt in ac.tickets[b] and
		some t1: prevs[t] | ac not in b.contains.t1 and dt.state.t1 = Expired and ac in b.contains.t)

}


run {}






