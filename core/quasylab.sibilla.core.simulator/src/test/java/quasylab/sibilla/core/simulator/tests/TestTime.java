/**
 * 
 */
package quasylab.sibilla.core.simulator.tests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;

import quasylab.sibilla.core.simulator.DefaultRandomGenerator;
import quasylab.sibilla.core.simulator.NetworkSimulationManager;
import quasylab.sibilla.core.simulator.SimulationEnvironment;
import quasylab.sibilla.core.simulator.ThreadSimulationManager;
import quasylab.sibilla.core.simulator.pm.PopulationModel;
import quasylab.sibilla.core.simulator.pm.PopulationRule;
import quasylab.sibilla.core.simulator.pm.PopulationState;
import quasylab.sibilla.core.simulator.pm.ReactionRule;
import quasylab.sibilla.core.simulator.pm.ReactionRule.Specie;
import quasylab.sibilla.core.simulator.sampling.SamplingCollection;
import quasylab.sibilla.core.simulator.sampling.SamplingFunction;
import quasylab.sibilla.core.simulator.sampling.StatisticSampling;
import quasylab.sibilla.core.simulator.serialization.SerializationType;
import quasylab.sibilla.core.simulator.server.ServerInfo;;

/**
 * @author loreti
 *
 */
public class TestTime {

	public final static int S = 0;
	public final static int E = 1;
	public final static int I = 2;
	public final static int R = 3;

	public final static int INIT_S = 99;
	public final static int INIT_E = 0;
	public final static int INIT_I = 1;
	public final static int INIT_R = 0;
	public final static double N = INIT_S + INIT_E + INIT_I + INIT_R;

	public final static double LAMBDA_E = 1;
	public final static double LAMBDA_I = 1 / 3.0;
	public final static double LAMBDA_R = 1 / 7.0;

	public final static int SAMPLINGS = 100;
	public final static double DEADLINE = 600;
	private static final int REPLICA = 1000;

	public static void main(String[] argv) throws FileNotFoundException, InterruptedException, UnknownHostException {
		List<Long> stats = new ArrayList<>();
		PrintStream out = new PrintStream(new FileOutputStream("thread_data.data", true));
		out.println("Concurrent tasks;pool size;average runtime;maximum runtime;minimum runtime");
		out.close();
		PrintStream out2 = new PrintStream(new FileOutputStream("run_data.data"));
		out2.println("Concurrent tasks;total runtime");
		for (int i = 1; i <= 1000; i++) { // i -> number of concurrent tasks
			for (int j = 0; j < 1; j++) { // j -> number of runs
				PopulationRule rule_S_E = new ReactionRule("S->E", new Specie[] { new Specie(S), new Specie(I) },
						new Specie[] { new Specie(E), new Specie(I) },
						s -> s.getOccupancy(S) * LAMBDA_E * (s.getOccupancy(I) / N));

				PopulationRule rule_E_I = new ReactionRule("E->I", new Specie[] { new Specie(E) },
						new Specie[] { new Specie(I) }, s -> s.getOccupancy(E) * LAMBDA_I);

				PopulationRule rule_I_R = new ReactionRule("I->R", new Specie[] { new Specie(I) },
						new Specie[] { new Specie(R) }, s -> s.getOccupancy(I) * LAMBDA_R);

				PopulationModel f = new PopulationModel();

				// f.addState( "initial" , initialState() );

				f.addRule(rule_S_E);
				f.addRule(rule_E_I);
				f.addRule(rule_I_R);

				StatisticSampling<PopulationState> fiSamp = StatisticSampling.measure("Fraction Infected", SAMPLINGS,
						DEADLINE, s -> s.getOccupancy(I) / N);
				StatisticSampling<PopulationState> frSamp = StatisticSampling.measure("Fraction Recovered", SAMPLINGS,
						DEADLINE, s -> s.getOccupancy(R) / N);

				// StatisticSampling<PopulationModel> eSamp = StatisticSampling.measure("#E",
				// SAMPLINGS, DEADLINE, s -> s.getCurrentState().getOccupancy(E)) ;
				// StatisticSampling<PopulationModel> iSamp = StatisticSampling.measure("#I",
				// SAMPLINGS, DEADLINE, s -> s.getCurrentState().getOccupancy(I)) ;
				// StatisticSampling<PopulationModel> rSamp = StatisticSampling.measure("#R",
				// SAMPLINGS, DEADLINE, s -> s.getCurrentState().getOccupancy(R)) ;

				// SimulationEnvironment<PopulationModel,PopulationState> sim = new
				// SimulationEnvironment<>( f );
				SimulationEnvironment sim = new SimulationEnvironment(
						NetworkSimulationManager
								.getNetworkSimulationManagerFactory(
										List.of(new ServerInfo(InetAddress.getByName("192.168.1.201"), 8080,
												SerializationType.DEFAULT)),
										"quasylab.sibilla.core.simulator.tests.TestTime"));

				SamplingFunction<PopulationState> sf = new SamplingCollection<>(fiSamp, frSamp);
				long startTime = System.nanoTime();
				sim.simulate(new DefaultRandomGenerator(), f, initialState(), sf, REPLICA, DEADLINE, true);
				long endTime = System.nanoTime() - startTime;
				fiSamp.printTimeSeries(new PrintStream("data/seir_" + REPLICA + "_" + N + "_FI_.data"), ';');
				frSamp.printTimeSeries(new PrintStream("data/seir_" + REPLICA + "_" + N + "_FR_.data"), ';');
				stats.add(endTime);
			} // j loop
			LongSummaryStatistics statistics = stats.stream().mapToLong(Long::valueOf).summaryStatistics();
			out2.println(i + ";" + statistics.getAverage());
			stats.clear();
			System.out.println(i);
		} // i loop
		out2.close();
	}

	public static PopulationState initialState() {
		return new PopulationState(new int[] { INIT_S, INIT_E, INIT_I, INIT_R });
	}
}
