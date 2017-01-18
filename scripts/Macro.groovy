sensor "bt" pin 9
ld "ld1" pin 12
ld "ld2" pin 11

state "ld1on" means ld1 becomes high
state "ld1off" means ld1 becomes low

state "ld2on" means ld2 becomes high
state "ld2off" means ld2 becomes low

state "off" means ld1 becomes low and ld2 becomes low

from ld1on to ld1off when button becomes high
from ld1off to ld1on when button becomes high


from ld2on to ld2off when button becomes high
from ld2off to ld2on when button becomes high


defineMacro "ld1Blink" from ld1on to ld1off
defineMacro "ld2Blink" from ld2on to ld2off

from ld1Blink to ld2Blink when 2.s
from ld2Blink to ld1Blink when 2.s

from off to ld1Blink when 2.s

initial off

export "MarcroBlinker"