sensor "bt" pin 9
led "ld" pin 12

state "on" means ld becomes high
state "off" means ld becomes low



from on to off when bt becomes high
from off to on when bt becomes high

initial off

exportToCompose "Sketch1"