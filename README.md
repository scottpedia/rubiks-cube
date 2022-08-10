# Rubiks-cube

A Java Application in an attempt to solve the Rubik's cube programmatically.

## Overview

I was thinking about using the concept of permutation to solve a Rubik's cube.
The idea is that at any state of the cube, there are 18 possible operations to get
into the next state. That number is computed by `3(axis) x 3(layers) x 2(directions) = 18`.
However, an improved version should only use 12, since the middle layer of each axis
can be omitted. So what a program can do, with an input of a cube's random state, is 
to compute all the derived possible states as in a tree diagram. So at the `nth` derived
generation there would be `18^n` possibilities. Ideally, after some time, there would be 
one state of the cube that is restored, ideally... 

However, the result is at the 5th generation the memory of my laptop runs out, since
all the new possibilities have to be cloned copies of the originals. 

So I came up with another solution which uses a certain number of `Cube` objects
and commit random operations at each one of them. After every so many steps the
process is restarted, but the number of objects stays the same. In the end it didn't
work out as well.

All the attempts are based on the assumption that through the brute-force type
of trying we can get a cube restored programmatically. But it seems that I was
wrong.