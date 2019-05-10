0.90::cookingLight:- activity(cooking),location(cooking_area).
0.70::cookingLight:- activity(cooking).
0.50::cookingLight:- location(cooking_area).

0.90::sinkLight:- activity(washing),location(near_sink).
0.70::sinkLight:- activity(washing).

0.90::dinningLight:- activity(dinning),location(dinning_table).
0.70::dinningLight:- activity(dinning).
0.50::dinningLight:- location(dinning_table).

0.90::ceilingLight:- activity(tidyingup).
0.50::ceilingLight.
