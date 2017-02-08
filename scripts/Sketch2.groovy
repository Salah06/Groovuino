sensor "bt" pin 9
buzzer "ld" pin 11

state "on" means ld becomes high
state "off" means ld becomes low


from on to off when bt becomes high
from off to on when bt becomes high


initial off

exportToCompose "Sketch2"