/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * The Minimum Utilization (MU) VM selection policy with Migration Control.
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
public class PowerVmSelectionPolicyMinimumUtilization extends PowerVmSelectionPolicy {

	/*
	 * (non-Javadoc)
	 * @see
	 * org.cloudbus.cloudsim.experiments.power.PowerVmSelectionPolicy#getVmsToMigrate(org.cloudbus
	 * .cloudsim.power.PowerHost)
	 */
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

                        float metric5=0;
                        if (CloudSim.clock()>= 1200){
                        //System.out.println("i am in");
                        float metric0 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / vm.getMips())*100;
                        float metric1 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-300) / vm.getMips())*100;
                        float metric2 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-600) / vm.getMips())*100;
                        float metric3 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-900) / vm.getMips())*100;
                        float metric4 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-1200) / vm.getMips())*100;

                        metric5=(float)((metric0+metric1+metric2+metric3+metric4)/10);
                        
                        }
                        

                        
			double metric = vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / vm.getMips();
			if (metric < minMetric) {
				minMetric = metric;
                                if (metric5<70){
				vmToMigrate = vm;
                                xx=1;
                                }else{
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
