/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * The Random Selection (RS) VM selection policy with Migration Control.
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
public class PowerVmSelectionPolicyRandomSelection extends PowerVmSelectionPolicy {
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
		int index1 = (new Random()).nextInt(migratableVms.size());
                int index=0;
                Vm vm=null;
                Vm vmToMigrate1=migratableVms.get(index1);
                //System.out.println(migratableVms.isEmpty());
               	while (migratableVms.isEmpty()==false) {
                    	if (migratableVms.isEmpty()) {
			break;
                        }
                        index = (new Random()).nextInt(migratableVms.size());
                        vm=migratableVms.get(index);
                        if (vm.isInMigration()) {
			migratableVms.remove(vm);
                        continue;
			}
                        float metric5=0;
                        if (CloudSim.clock()>= 1200){
                        float metric0 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) / vm.getMips())*100;
                        float metric1 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-300) / vm.getMips())*100;
                        float metric2 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-600) / vm.getMips())*100;
                        float metric3 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-900) / vm.getMips())*100;
                        float metric4 = (float)(vm.getTotalUtilizationOfCpuMips(CloudSim.clock()-1200) / vm.getMips())*100;
                        metric5=(float)((metric0+metric1+metric2+metric3+metric4)/5);
                        }
                        if (metric5<85){
			return migratableVms.get(index);
                        }else{
                        //System.out.println("removed");
                        migratableVms.remove(vm);
			} 
                }
		return vmToMigrate1; 
	}

}
