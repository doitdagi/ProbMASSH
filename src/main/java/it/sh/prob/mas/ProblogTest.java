package it.sh.prob.mas;

import it.sh.prob.mas.utilities.problog.JProblog;

public class ProblogTest {

	public static void main(String[] args) {

		String s = "0.95::loc(near_sink).\n" + "0.95::act(cooking).\n" + "0.90::cookingLight:- act(cooking),\n"
				+ "loc(cooking_area).\n" + "0.70::cookingLight:- act(cooking).\n"
				+ "0.50::cookingLight:- loc(cooking_area).\n" + "0.90::sinkLight:- act(washing),\n"
				+ "loc(near_sink).\n" + "0.70::sinkLight:- act(washing).\n" + "0.50::sinkLight:- loc(near_sink).\n"
				+ "0.90::dinningLight:- act(dinning),\n" + "loc(dinning_table).\n"
				+ "0.70::dinningLight:- act(dinning).\n" + "0.50::dinningLight:- loc(dinning_table).\n"
				+ "0.90::ceilingLight:- act(tidyingup).\n" + "0.50::ceilingLight.\n" + "query(sinkLight).\n" + "";

		JProblog problog = new JProblog();
		String result = problog.apply(s);
		System.out.println(result);
	}

}
