sensor "bt" pin 9
led "ld1" pin 12
led "ld2" pin 11

state "ld1on" means ld1 becomes high
state "ld1off" means ld1 becomes low

state "ld2on" means ld2 becomes high
state "ld2off" means ld2 becomes low

state "off" means ld1 becomes low and ld2 becomes low



defineMacro "blink" using "state1" to "state2" when bt becomes high and "state2" to "state1" when bt becomes high

applyMacro blink using ld1on and ld1off

from off to ld1on when 3.s

initial off

export "MarcroBlinker"