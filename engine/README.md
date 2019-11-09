# Matching Core

Containss the core logic for matching trades in a order book

#### OrderBook

Matching always happens at the top most row where the bid and the ask is the closest

 

| bid | ask |
|:-----:|:-----:|
|  44 |  34 |
|  44.5 |  33.9 |
|  44.9 |  33.7 |
|  45 |  32.8 |
|  46 |  31 |
|  46 |  31 |
|  47 |  30 |

| ask | bid |
|:-----:|:-----:|
|  44 |  34 |
|  44.5 |  33.9 |
|  44.9 |  33.7 |
|  45 |  32.8 |
|  46 |  31 |
|  46 |  31 |
|  47 |  30 |
