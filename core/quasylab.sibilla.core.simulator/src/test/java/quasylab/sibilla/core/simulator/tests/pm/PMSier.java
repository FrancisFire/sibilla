/**
 * 
 */
package quasylab.sibilla.core.simulator.tests.pm;

import quasylab.sibilla.core.models.pm.*;
import quasylab.sibilla.core.simulator.DefaultRandomGenerator;
import quasylab.sibilla.core.simulator.SimulationEnvironment;
import quasylab.sibilla.core.simulator.sampling.Measure;
import quasylab.sibilla.core.simulator.sampling.SamplingCollection;
import quasylab.sibilla.core.simulator.sampling.SamplingFunction;
import quasylab.sibilla.core.simulator.sampling.StatisticSampling;

import java.util.LinkedList;

/**
 * @author loreti
 *
 */
public class PMSier {

	public static final int S_INDEX = 0;
	public static final int I_INDEX = 1;
	public static final int R_INDEX = 2;

	public static final double ALPHA = 0.1;
	public static final double BETA = 0.1;
	public static final double GAMMA = 0.2;

	private static final int SIZE = 100;

	public static void main(String[] argv) throws InterruptedException {
		SimulationEnvironment sim = new SimulationEnvironment();
		PopulationModel model = buildPopulationModel();
		StatisticSampling<PopulationState> sSamp = new StatisticSampling<>(1000, 0.1, new Measure<PopulationState>() {

			@Override
			public double measure(PopulationState t) {
				// TODO Auto-generated method stub
				return t.getOccupancy(S_INDEX);
			}

			@Override
			public String getName() {
				return "S";
			}

		});
		StatisticSampling<PopulationState> iSamp = new StatisticSampling<>(1000, 0.1, new Measure<PopulationState>() {

			@Override
			public double measure(PopulationState t) {
				// TODO Auto-generated method stub
				return t.getOccupancy(I_INDEX);
			}

			@Override
			public String getName() {
				return "I";
			}

		});
		StatisticSampling<PopulationState> rSamp = new StatisticSampling<>(1000, 0.1, new Measure<PopulationState>() {

			@Override
			public double measure(PopulationState t) {
				// TODO Auto-generated method stub
				return t.getOccupancy(R_INDEX);
			}

			@Override
			public String getName() {
				return "R";
			}

		});

		SamplingFunction<PopulationState> sf = new SamplingCollection<PopulationState>(sSamp, iSamp, rSamp);
		sim.simulate(new DefaultRandomGenerator(), model, new PopulationState(new int[] { SIZE - 1, 1, 0 }), sf, 1, 100,
				true);
	}

	private static PopulationModel buildPopulationModel() {
		PopulationModel m = new PopulationModel();
		m.addRules(buildRules());
		m.addState("init", new PopulationState(new int[] { SIZE - 1, 1, 0 }));
		return m;
	}

	private static LinkedList<PopulationRule> buildRules() {
		LinkedList<PopulationRule> rules = new LinkedList<>();

		rules.add(new ReactionRule("S,I->I,I",
				new Population[] { new Population(S_INDEX, 1), new Population(I_INDEX, 1) },
				new Population[] { new Population(I_INDEX, 2) },
				(s -> s.getOccupancy(S_INDEX) * s.getOccupancy(I_INDEX) * BETA)));
		rules.add(new ReactionRule("S->I", new Population[] { new Population(S_INDEX, 1) },
				new Population[] { new Population(I_INDEX, 1) },
				(s -> (s.getOccupancy(I_INDEX) == 0 ? s.getOccupancy(S_INDEX) * BETA : 0.0))));
		rules.add(new ReactionRule("I->R", new Population[] { new Population(I_INDEX, 1) },
				new Population[] { new Population(R_INDEX, 1) },
				(s -> s.getOccupancy(I_INDEX) * ALPHA)));
		rules.add(new ReactionRule("R->S", new Population[] { new Population(R_INDEX, 1) },
				new Population[] { new Population(S_INDEX, 1) },
				(s -> s.getOccupancy(R_INDEX) * GAMMA)));

		return rules;
	}

}
