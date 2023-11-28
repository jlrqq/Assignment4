Assignment 4
------------

# Team Members: Joey & Lorena

# GitHub link to your (forked) repository (if submitting through GitHub)
https://github.com/jlrqq/Assignment4

# Task 4

1. What is the causal consistency? Explain it using the happened-before relation.
> Causal consistency is weaker than strong consistency models (sequential consistency) but stronger than eventual consistency. 
In a strong consistency model, all operations are executed in a sequential order after each other. 
Causal consistency relaxes this restriction since only the maintenance of a causal relationship between operations is essential in the sense of a "happend before" relationship: 
If operation A happens before B, then this causal relationship is maintained across the whole system, all processes see A before B. 
If two operations are concurrent (=independent, not causally related, there is no message exchange between the operations that happen in different processes), they can be seen in different orders by different processes. 
Compared to eventual consistency, a weak consistency model, which gives no guranatees about the order, the happened-before relationship in causal consistency is always respected.
Lamport's logical clocks follow the happend-before relationship, which is transitive "a-->b and b-->c, then a --> c (p.541)"
(source: p.401-403, p.541 in "Distributed Systems, 4 th edition, MAARTEN VAN STEEN & ANDREW S. TANENBAUM)


2. You are responsible for designing a distributed system that maintains a partial ordering of operations on a data store (for instance, maintaining a time-series log database receiving entries from multiple independent processes/sensors with minimum or no concurrency). When would you choose Lamport timestamps over vector clocks? Explain your argument. 
   What are the design objectives you can meet with both?
> Vector & Lamport clocks are both logical clocks capturing the order of events consistent with causality. Both may be used in systems designed to be fault-tolerant and both are able to detect concurrency in general.
Vector clocks are an enhancement of the Lamport clocks since they maintain a vector of logical clocks for every process in the system. While Lamport clocks cannot infer causality between events, vector clocks provide the causal history of the events which lets us determine, whether two events are concurrent or causally dependent.
Depending on the system's requirements, Lamport clocks may still be the more efficient solution since they provide simple timestamps, whereas vector clocks require more space when the number of processes increases since they require an entry for every single process.
Lamport clocks are more efficient in simpler systems, like a log database, where the scalability of the system is important and where the simple ordering of events is sufficient.
Since in a log database, the chronological order of all sensors sending data should be captured, a lamport clock implementation should be sufficient. 


3.Vector clocks are an extension of the Lamport timestamp algorithm. However, scaling a vector clock to handle multiple processes can be challenging. Propose some solutions to this and explain your argument. 
> Challenges of scalability arise for vector clocks due to the fact, that they are often implemented as an array, which requires a fixed and known size of the vector (=number of processes that will be tracked) already in advance. 
Dynamic vector clocks as an extension of vector clocks allow a flexible handling of dynamic processes since they are able to grow / expand dynamically as the amount of processes rises.
It is important to implement a "garbage collection" to get rid of no longer active processes. 
Dynamic vector clocks are especially important in  distributed systems, where the number of participating processes changes dynamically and very frequent according to the demand. 
(source: Article "Dynamic vector clocks: abstract & 3.3 dynamic vector clocks)
