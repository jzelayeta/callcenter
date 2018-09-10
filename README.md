# CallCenter Implementation

## Elements of the Model

    * Attendant
    * Attendant Priority
    * Call
    * Dispatcher
    
## Attendant

Model a person in charge of interact with customers. 
Its main responsibility is to answer incoming calls.
Two attendants may have different priorities. 

## Attendant Priority

This call center manages three priority for its attendants:

    * OPERATOR
    * SUPERVISOR
    * DIRECTOR

being DIRECTOR the actual highest priority and OPERATOR the lowest.

## Call

This class represent a Call itself with audit information such as timestamps 
for start and end of call, duration and the Attendant who served it. 

Notice that the start timestamp is when communication with the Attendant starts, 
so the duration is not the total time that customer was on the phone, but the time
that the attendant spent on it.

## Dispatcher

This is the core. Dispatcher has the following responsibilities: 

    * Manage attendants
    * Manage pending calls
    * Manage finished calls
    
 When a call incomes to Dispatcher, this one will pick the first Attendant with
 lowest priority that is idle. 
 
 In case all Attendants are occupied and a new call arrives to the dispatcher, this one will stay
 on the pending calls waiting an Attendant to be idle and start communication. This means
 that Dispatcher can manage more calls than the Attendants capacity.
 
 Dispatcher makes use of a `ThreadPool` to reuse threads among it processes different calls. 
 Each time a call is dispatched a new `Thread` will be picked up form that pool, and an Attendant will
 execute it.
 
 `BlockingQueue` avoid the dispatcher to end with inconsistency, or worst than that, on deadlock due to
 concurrency.s
 
 ## Use of Observer Pattern
 
 Since `take()` method on `PriorityBlockingQueue<Attendant>` removes the attendants from that queue, we'll need that
 attendant to be inserted again after it finishes the call, otherwise Attendants will only be able to answer only one call, 
 and this call center will only be able to dispatch N calls, being N the number of Attendants.
 
 That's why `Attendant` extends `Observable` and `Dispatcher` implements `Observable`, so every time an Attendant finish a call
 it will notify its Observer. And the Dispatcher, every time that is notified from an Attendant, will put it back to
 attendants queue to be ready for answer more calls.
 