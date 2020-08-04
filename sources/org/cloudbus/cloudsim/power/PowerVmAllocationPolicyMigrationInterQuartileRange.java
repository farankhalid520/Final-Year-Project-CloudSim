/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * The Inter Quartile Range (IQR) VM allocation policy.
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
public class PowerVmAllocationPolicyMigrationInterQuartileRange extends
		PowerVmAllocationPolicyMigrationAbstract {

	/** The safety parameter. */
	private double safetyParameter = 0;
	public static double slaFailcount=0;

	/** The fallback vm allocation policy. */
	private PowerVmAllocationPolicyMigrationAbstract fallbackVmAllocationPolicy;

	/**
	 * Instantiates a new power vm allocation policy migration mad.
	 * 
	 * @param hostList the host list
	 * @param vmSelectionPolicy the vm selection policy
	 * @param safetyParameter the safety parameter
	 * @param utilizationThreshold the utilization threshold
	 */
	public PowerVmAllocationPolicyMigrationInterQuartileRange(
			List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double safetyParameter,
			PowerVmAllocationPolicyMigrationAbstract fallbackVmAllocationPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy);
		setSafetyParameter(safetyParameter);
		setFallbackVmAllocationPolicy(fallbackVmAllocationPolicy);
	}

	/**
	 * Instantiates a new power vm allocation policy migration mad.
	 * 
	 * @param hostList the host list
	 * @param vmSelectionPolicy the vm selection policy
	 * @param safetyParameter the safety parameter
	 */
	public PowerVmAllocationPolicyMigrationInterQuartileRange(
			List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double safetyParameter,
			PowerVmAllocationPolicyMigrationAbstract fallbackVmAllocationPolicy) {
		super(hostList, vmSelectionPolicy);
		setSafetyParameter(safetyParameter);
		setFallbackVmAllocationPolicy(fallbackVmAllocationPolicy);
	}

	/**
	 * Checks if is host over utilized.
	 * Modified using Migration Control
	 * @param _host the _host
	 * @return true, if is host over utilized
	 */
	@Override
	protected boolean isHostOverUtilized(PowerHost host) {
		PowerHostUtilizationHistory _host = (PowerHostUtilizationHistory) host;
		double upperThreshold = 0;
		try {
			upperThreshold = 1 - getSafetyParameter() * getHostUtilizationIqr(_host);
                        //System.out.println("upperthre"+upperThreshold+ "getHostUtilizationIqr(_host): " + getHostUtilizationIqr(_host) );
		} catch (IllegalArgumentException e) {
			return getFallbackVmAllocationPolicy().isHostOverUtilized(host);
		}
		addHistoryEntry(host, upperThreshold);
		double totalRequestedMips = 0;
                double totalPredictedMips=0;
                double stddev=0;
		for (Vm vm : host.getVmList()) {
                        if (vm.priority>0) totalRequestedMips += vm.getMips();
                        else totalRequestedMips += vm.getCurrentRequestedTotalMips();
                        PowerVm _vm=(PowerVm) vm;
                        totalPredictedMips+=_vm.getUtilizationMean()+Math.sqrt(_vm.getUtilizationVariance());
                        
		}
                
		double utilization = totalRequestedMips / host.getTotalMips();
                if(utilization >=1)  slaFailcount+=1;
                double utilizationPrediction = totalPredictedMips / host.getTotalMips();
                if ((utilization > upperThreshold && utilizationPrediction >=1) || (utilization>=1))
                {
                 return true;                
                }
                else return false;
                
		//return utilization > upperThreshold;
	}

      	public double returnSlaFailCount() {
		return slaFailcount;
	}


	/**
	 * Gets the host utilization iqr.
	 * 
	 * @param host the host
	 * @return the host utilization iqr
	 */
	protected double getHostUtilizationIqr(PowerHostUtilizationHistory host) throws IllegalArgumentException {
		double[] data = host.getUtilizationHistory();
		if (MathUtil.countNonZeroBeginning(data) >= 12) { // 12 has been suggested as a safe value
			return MathUtil.iqr(data);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Sets the safety parameter.
	 * 
	 * @param safetyParameter the new safety parameter
	 */
	protected void setSafetyParameter(double safetyParameter) {
		if (safetyParameter < 0) {
			Log.printLine("The safety parameter cannot be less than zero. The passed value is: "
					+ safetyParameter);
			System.exit(0);
		}
		this.safetyParameter = safetyParameter;
	}

	/**
	 * Gets the safety parameter.
	 * 
	 * @return the safety parameter
	 */
	protected double getSafetyParameter() {
		return safetyParameter;
	}

	/**
	 * Sets the fallback vm allocation policy.
	 * 
	 * @param fallbackVmAllocationPolicy the new fallback vm allocation policy
	 */
	public void setFallbackVmAllocationPolicy(
			PowerVmAllocationPolicyMigrationAbstract fallbackVmAllocationPolicy) {
		this.fallbackVmAllocationPolicy = fallbackVmAllocationPolicy;
	}

	/**
	 * Gets the fallback vm allocation policy.
	 * 
	 * @return the fallback vm allocation policy
	 */
	public PowerVmAllocationPolicyMigrationAbstract getFallbackVmAllocationPolicy() {
		return fallbackVmAllocationPolicy;
	}

}
