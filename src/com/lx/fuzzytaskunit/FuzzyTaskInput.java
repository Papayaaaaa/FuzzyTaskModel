package com.lx.fuzzytaskunit;

/**
 * @author Lx
 * 模糊任务推理输入项（evidence）包含天气，地形，太阳辐射，洪涝类型，洪涝响应级别，洪涝观测阶段，观测时间范围，观测空间范围
 */
public class FuzzyTaskInput {
	private String Topography="chengshi";
	private String CloudCover="low";
	private String SolarRadiation="low";
	private String FloodResponseLevel="II";
	private String FloodStage="Response";
	private String FloodType="neilao";
	private String observedtime="";
	private String observedspace="";
	private String ObservedSpaceScale="";
	private String ObservedTimeScale="";
/**
	 * @return the topography
	 */
	public String getTopography() {
		return Topography;
	}

	/**
	 * @param topography the topography to set
	 */
	public void setTopography(String topography) {
		Topography = topography;
	}



/**
 * @return the cloudCover
 */
public String getCloudCover() {
	return CloudCover;
}

/**
 * @param cloudCover the cloudCover to set
 */
public void setCloudCover(String cloudCover) {
	CloudCover = cloudCover;
}

/**
 * @return the solarRadiation
 */
public String getSolarRadiation() {
	return SolarRadiation;
}

/**
 * @param solarRadiation the solarRadiation to set
 */
public void setSolarRadiation(String solarRadiation) {
	SolarRadiation = solarRadiation;
}

/**
 * @return the floodResponseLevel
 */
public String getFloodResponseLevel() {
	return FloodResponseLevel;
}

/**
 * @param floodResponseLevel the floodResponseLevel to set
 */
public void setFloodResponseLevel(String floodResponseLevel) {
	FloodResponseLevel = floodResponseLevel;
}

/**
 * @return the floodStage
 */
public String getFloodStage() {
	return FloodStage;
}

/**
 * @param floodStage the floodStage to set
 */
public void setFloodStage(String floodStage) {
	FloodStage = floodStage;
}

/**
 * @return the floodType
 */
public String getFloodType() {
	return FloodType;
}

/**
 * @param floodType the floodType to set
 */
public void setFloodType(String floodType) {
	FloodType = floodType;
}

/**
 * @return the observedtime
 */
public String getObservedtime() {
	return observedtime;
}

/**
 * @param observedtime the observedtime to set
 */
public void setObservedtime(String observedtime) {
	this.observedtime = observedtime;
}

/**
 * @return the observedspace
 */
public String getObservedspace() {
	return observedspace;
}

/**
 * @param observedspace the observedspace to set
 */
public void setObservedspace(String observedspace) {
	this.observedspace = observedspace;
}

/**
 * @return the observedSpaceScale
 */
public String getObservedSpaceScale() {
	return ObservedSpaceScale;
}

/**
 * @param observedSpaceScale the observedSpaceScale to set
 */
public void setObservedSpaceScale(String observedSpaceScale) {
	ObservedSpaceScale = observedSpaceScale;
}

/**
 * @return the observedTimeScale
 */
public String getObservedTimeScale() {
	return ObservedTimeScale;
}

/**
 * @param observedTimeScale the observedTimeScale to set
 */
public void setObservedTimeScale(String observedTimeScale) {
	ObservedTimeScale = observedTimeScale;
}

}
