A basic rocket "game" that's actually more of a simulation. This project was mainly to brush up on Java and Android. I started by building a simple 2D physics engine to handle orbits, and added a simulated rocket based on real aspects of rocketry (Isp, fuel flow, etc) to allow you to pilot a rocket from the surface of a planet into orbit (or beyond, though there's only one planet so there's nothing to see beyond).

## Fun features
* Multiple stages and the ability to switch between them (hoping to allow for landing the first stage SpaceX-style)
* Time warping to speed up the simulation
* Technically supports multiple orbiting bodies, but doesn't handle the three-body or n-body problem (each orbit is independent)
* An interesting UI element to allow for controlling angle of a stage while also controlling throttle
