0.90::cookingLight:- act(cooking),loc(cooking_area).
0.70::cookingLight:- act(cooking).
0.50::cookingLight:- loc(cooking_area).

0.90::sinkLight:- act(washing),loc(near_sink).
0.70::sinkLight:- act(washing).
0.50::sinkLight:- loc(near_sink).

0.90::dinningLight:- act(dinning),loc(dinning_table).
0.70::dinningLight:- act(dinning).
0.50::dinningLight:- loc(dinning_table).

0.90::ceilingLight:- act(tidyingup).
0.50::ceilingLight.
