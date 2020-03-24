/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package quasylab.sibilla.core.simulator;

import java.util.function.Consumer;

import org.apache.commons.math3.random.RandomGenerator;
import quasylab.sibilla.core.simulator.pm.State;

/**
 * @author belenchia
 *
 */
public class SequentialSimulationManager<S extends State> extends SimulationManager<S> {



    
    public static final SimulationManagerFactory getSequentialSimulationManagerFactory() {
    	return new SimulationManagerFactory() {
   		
			@Override
			public <S extends State> SimulationManager<S> getSimulationManager(RandomGenerator random, Consumer<Trajectory<S>> consumer) {
				return new SequentialSimulationManager<>(random, consumer);
			}
    	};
		
	}
    

    public SequentialSimulationManager(RandomGenerator random, Consumer<Trajectory<S>> consumer) {
    	super(random,consumer);
	}

    @Override
    public void simulate(SimulationUnit<S> unit) {
        SimulationTask<S> task = new SimulationTask<>(getRandom(), unit);
        handleTrajectory(task.get());
    }

 
    @Override
    public void join(){
        return;
    }

	@Override
	protected void start() {
        return;
	}
    
    @Override
	protected void handleTrajectory( Trajectory<S> trj ) {
		getExecutionTimes().add(trj.getGenerationTime());
		getConsumer().accept(trj);
		propertyChange("progress", getExecutionTimes().size());
	}

    
}