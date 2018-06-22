package com.lx.fuzzytaskunit;

import java.util.List;


/**
 * @author Lx
 * 模糊推理模型中输出部分，包含光谱分辨率，空间分辨率，重放周期，波段类型，观测时间尺度，观测空间尺度
 *
 */
public class FuzzyTaskOutput {

	private double[] RadiationResolution=new double[3];
	private double[] AccurateObservedTimeScale=new double[6];
	private double[] AccurateObservedSpaceScale=new double[3];
	private double[] RevisitCycle=new double[6];
	private double[] SpaceResolution=new double[7];
	private double[] BandType=new double[15];
	private double[] SpaceBoundery=new double[4];
	/**
	 * @return the radiationResolution
	 */
	public double[] getRadiationResolution() {
		return RadiationResolution;
	}
	/**
	 * @param radiationResolution the radiationResolution to set
	 */
	public void setRadiationResolution(double[] radiationResolution) {
		RadiationResolution = radiationResolution;
	}
	/**
	 * @return the accurateObservedTimeScale
	 */
	public double[] getAccurateObservedTimeScale() {
		return AccurateObservedTimeScale;
	}
	/**
	 * @param accurateObservedTimeScale the accurateObservedTimeScale to set
	 */
	public void setAccurateObservedTimeScale(double[] accurateObservedTimeScale) {
		AccurateObservedTimeScale = accurateObservedTimeScale;
	}
	/**
	 * @return the accurateObservedSpaceScale
	 */
	public double[] getAccurateObservedSpaceScale() {
		return AccurateObservedSpaceScale;
	}
	/**
	 * @param accurateObservedSpaceScale the accurateObservedSpaceScale to set
	 */
	public void setAccurateObservedSpaceScale(double[] accurateObservedSpaceScale) {
		AccurateObservedSpaceScale = accurateObservedSpaceScale;
	}
	/**
	 * @return the revisitCycle
	 */
	public double[] getRevisitCycle() {
		return RevisitCycle;
	}
	/**
	 * @param revisitCycle the revisitCycle to set
	 */
	public void setRevisitCycle(double[] revisitCycle) {
		RevisitCycle = revisitCycle;
	}
	/**
	 * @return the spaceResolution
	 */
	public double[] getSpaceResolution() {
		return SpaceResolution;
	}
	/**
	 * @param spaceResolution the spaceResolution to set
	 */
	public void setSpaceResolution(double[] spaceResolution) {
		SpaceResolution = spaceResolution;
	}
	/**
	 * @return the bandType
	 */
	public double[] getBandType() {
		return BandType;
	}
	/**
	 * @param bandType the bandType to set
	 */
	public void setBandType(double[] bandType) {
		BandType = bandType;
	}
	/**
	 * @return the spaceBoundery
	 */ 
	public double[] getSpaceBoundery() {
		return SpaceBoundery;
	}
	/**
	 * @param spaceBoundery the spaceBoundery to set
	 */
	public void setSpaceBoundery(double[] spaceBoundery) {
		SpaceBoundery = spaceBoundery;
	}
	

}
