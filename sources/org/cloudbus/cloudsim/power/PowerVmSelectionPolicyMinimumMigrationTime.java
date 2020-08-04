/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * The Minimum Migration Time (MMT) VM selection policy with Migration Control.
 * 
 * If you are using any algorithms, policies or workload included in the power package, please cite
 * the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class PowerVmSelectionPolicyMinimumMigrationTime extends PowerVmSelectionPolicy {

	/*
	 * (non-Javadoc)
	 * @see
	 * org.cloudbus.cloudsim.experiments.power.PowerVmSelectionPolicy#getVmsToMigrate(org.cloudbus
	 * .cloudsim.power.PowerHost)
	 */
	//MMT with Migration Control
	
	@Override
	public Vm getVmToMigrate(PowerHost host) {
		List<PowerVm> migratableVms = getMigratableVms(host);
		if (migratableVms.isEmpty()) {
			return null;
		}
		Vm vmToMigrate = null; 
		Vm vmToMigrate1 = null; 
                int xx=0;
		
		double minMetric = Double.MAX_VALUE;
		for (Vm vm : migratableVms) {
			if (vm.isInMigration()) {
				continue;
			}
			
			 double metric5=0;
             List<Double> metric = new ArrayList<Double>();
             if (CloudSim.clock()>= 1200){
             
             metric.add((double)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / vm.getMips())*100);
             metric.add((double)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-300) / vm.getMips())*100);
             metric.add((double)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-300) / vm.getMips())*100);
             metric.add((double)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-600) / vm.getMips())*100);
             metric.add((double)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-900) / vm.getMips())*100);
             
             metric5=MathUtil.mean(metric); //mean of the metric list
             }
             
             PowerVm _vm=(PowerVm) vm;
             
             double metric6 = Math.sqrt(_vm.getUtilizationVariance()); //standard deviation
             metric6=MathUtil.stDev(metric);
			
			double metric1 = vm.getRam();
			if (metric1 < minMetric) {
				minMetric = metric1;
                if (metric5<80){
                
                vmToMigrate = vm;
                xx=1;
                }
                else
                {
                 vmToMigrate1 = vm;
                }
			}
		}
		
		if (xx==1){
            return vmToMigrate;  
            
            }else {

            return vmToMigrate1;                
            }

	}

}
