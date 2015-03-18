# Dredd Rule Engine #

Dredd was created to be a simple way to detach application business logic in order to create a decision tree model best for visualize and perhaps easy to understand and maintain.

### Should I use a Rules Engine? ###

* A rules engine is all about providing an alternative computational model. Instead of the usual imperative model, commands in sequence with conditionals and loops, it provides a list of production rules. Each rule has a condition and an action - simplistically you can think of it as a bunch of if-then statements.

* As in many places, testing is often undervalued here, but implicit behavior makes testing more important - and it needs to be done with production data.

* One common discussion concerns encapsulating business logic (i.e., rules) within objects or services. Services make sense for decisions and compliance that are orchestrated by the business process. If a requirement, regulation, policy or rule spans many classes in the model and many tasks within the process, externalized rules are strongly indicated.


### Why  I do not use Drools ? ###

* This project was born in a project where payload size of jar, memory consumption , cpu utilization and easy maintenance was a rule of thumb , thus Drools was a huge and overweight solution for our "simple" solution
* There is a lot of work in order to make Drools run in Android , done by good people at community, but honestly regarding all the weird workarounds I not felt comfortable to go with those "stitched" tires.
* A lot of dependencies that we ain't gonna need and will just augment the size of final artifact .

### And about good principles of OOP? ###

* We just follow the SOLID principles
* Try to avoid over engineering  , I mean, create a lot of classes, interfaces just for sake of architecture.
* We need to maintain the components easy to understand, decoupled and tolerate to changes.

### Reference ###

* [InfoQ article](http://www.infoq.com/news/2007/12/haley)
* [Martin Fowler article about Rule Engines](http://martinfowler.com/bliki/RulesEngine.html)
* [Rules Engine on mobile platform : Getting Rule engine to work on Mobile Platform](http://tech-voyage.blogspot.com.br/2011/06/getting-rule-engine-to-work-on-mobile.html?m=1)
*
* [SOLID revisited](http://zeroturnaround.com/rebellabs/object-oriented-design-principles-and-the-5-ways-of-creating-solid-applications/)