sensor "bt" pin 9
led "ld1" pin 12
led "ld2" pin 11

state "ld1on" means ld1 becomes high
state "ld1off" means ld1 becomes low

state "ld2on" means ld2 becomes high
state "ld2off" means ld2 becomes low

state "off" means ld1 becomes low and ld2 becomes low

from ld1on to ld1off when bt becomes high
from ld1off to ld1on when bt becomes high


from ld2on to ld2off when bt becomes high
from ld2off to ld2on when bt becomes high


defineMacro "ld1Blink" from ld1on to ld1off
defineMacro "ld2Blink" from ld2on to ld2off



from off to ld1Blink when 3.s

initial off

export "MarcroBlinker"