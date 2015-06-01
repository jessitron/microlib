# microlib

Bring in tiny dependencies, like utility functions, bringing the code into your project.



## Purpose

Maven-style dependency resolution, where each dependency is brought in only once no matter how many times it's used,
have their place. Other times, they're annoying - there's version resolution, backwards compatibility issues.

Sometimes all you want is a little utility function. You can either keep writing it over and over, or you can put it in
a library. It's cut-and-paste hell, or transitive-dependency hell. 

Microlibraries (or "libbits," library-bits) try to take the hell out of cut-and-paste hell.

A microlibrary exposes (hopefully) one function.
It brings in no dependencies that don't already exist in your project. (not enforced by this program)
It comes with its own tests, which execute alongside your tests. Basically, it's a standardized utility function.

Compared to cut and paste, microlibraries
 * are standardized
 * come with tests
 * can be upgraded
 
In my dream world, they're available online in a central repo, with ratings and endorsements.

Compared to maven-style dependencies, microlibraries
 * never introduce version conflicts, because they're imported each time
 * are just the right size: no grouping, no big libraries for a few functions
 * upgrade independently. No adjusting usages of other functions because you wanted to upgrade this one.

## Usage

This program is an MVP -- meaning, a test. It copies source files from a microlibrary project to another project, 
changing the namespaces, and that's it.  


    ┌────────────────────────────────┐                                                 
    │destination-project             │                                                 
    │  ┌────┐                        │                                                 
    │  │src │                        │                                                 
    │  └────┘                        │               ┌────────────────────────────────┐
    │    ┌───────────────┐           │               │new-libbit                      │
    │    │ project-name  │           │     cut &     │   ┌────┐                       │
    │    └───────────────┘           │     paste     │   │src │                       │
    │      ┌ ─ ─ ─ ─ ─ ─ ─ ─         │     like a    │   └────┘                       │
    │       ┌────────┐      │        │      pro!     │     ┌────────┐                 │
    │      ││ libbit │               │               │     │ libbit │                 │
    │       └────────┘      │        │               │     └────────┘                 │
    │      │  new_libbit.clj ◀───────┼───────────────┼──────── new_libbit.clj         │
    │       ─ ─ ─ ─ ─ ─ ─ ─ ┘        │               │   ┌─────┐                      │
    │  ┌─────┐                       │               │   │test │                      │
    │  │test │                       │               │   └─────┘                      │
    │  └─────┘                       │               │      ┌────────┐                │
    │     ┌───────────────┐          │               │      │ libbit │                │
    │     │ project-name  │          │               │      └────────┘                │
    │     └───────────────┘          │          ┌────┼─────────new_libbit_test.clj    │
    │        ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐ │          │    │                                │
    │         ┌────────┐             │          │    └────────────────────────────────┘
    │        ││ libbit │           │ │          │                                      
    │         └────────┘             │          │                                      
    │        │ new_libbit_test.clj ◀─┼──────────┘                                      
    │         ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─  │                                                 
    └────────────────────────────────┘                                                 


If you want to play with this, clone this project; create or clone a microlibrary; have another Clojure project 
that wants to use the microlibrary, and then run this project:
  
    lein run -l <path-to-microlibrary> -d <path-to-destination-project> [-n <name-of-microlib>] [-d <name-of-destination-project]
    
The name of the microlibrary and the destination project default to their directory names.

## Plans

It would be awesome if libbits were a thing, with a program that fetched them from the internet and a site for finding them,
with search and ratings (so you could tell the tried ones from the new ones). And enforcement of what to make. 
And a lein template. And upgrade enforcement, so if you did change the code after it was copied it would refuse to upgrade.
Checks that there aren't any libraries needed by the libbit than you already have in your project.

It could happen.

## License

Copyright © 2015 Jessica Kerr

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
