constraint led to max with 3
constraint led to min with 4

constraint sensor to min with 1
constraint buzzer to max with 1



sensor "bt" pin 9
led "ld1" pin 12
led "ld2" pin 11

buzzer "bz" pin 15

state "off" means ld1 becomes low and ld2 becomes low

initial off

export "Ergonomic"