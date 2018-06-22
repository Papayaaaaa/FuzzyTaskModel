package com.jsmile.test;

import java.awt.Color;
import java.util.List;

import com.lx.fuzzytaskunit.FuzzyTaskInput;
import com.lx.fuzzytaskunit.FuzzyTaskOutput;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import smile.Network;
import smile.SMILEException;
import smile.ValueOfInfo;

public class BNTest {
	public static void CreateNetwork() {
		 try {
		   Network net = new Network();
		   
		   // Creating node "Success" and setting/adding outcomes:
		   net.addNode(Network.NodeType.Cpt, "Success");
		   net.setOutcomeId("Success", 0, "Success");
		   net.setOutcomeId("Success", 1, "Failure");
		   
		   // Creating node "Forecast" and setting/adding outcomes:
		   net.addNode(Network.NodeType.Cpt, "Forecast");  
		   net.addOutcome("Forecast", "Good");
		   net.addOutcome("Forecast", "Moderate");
		   net.addOutcome("Forecast", "Poor");
		   net.deleteOutcome("Forecast", 0);
		   net.deleteOutcome("Forecast", 0);
		   
		   // Adding an arc from "Success" to "Forecast":
		   net.addArc("Success", "Forecast");
		   
		   // Filling in the conditional distribution for node "Success". The 
		   // probabilities are:
		   // P("Success" = Success) = 0.2
		   // P("Success" = Failure) = 0.8
		   double[] aSuccessDef = {0.2, 0.8};
		   net.setNodeDefinition("Success", aSuccessDef);
		   
		   // Filling in the conditional distribution for node "Forecast". The 
		   // probabilities are:
		   // P("Forecast" = Good | "Success" = Success) = 0.4
		   // P("Forecast" = Moderate | "Success" = Success) = 0.4
		   // P("Forecast" = Poor | "Success" = Success) = 0.2
		   // P("Forecast" = Good | "Success" = Failure) = 0.1
		   // P("Forecast" = Moderate | "Success" = Failure) = 0.3
		   // P("Forecast" = Poor | "Success" = Failure) = 0.6
		   double[] aForecastDef = {0.4, 0.4, 0.2, 0.1, 0.3, 0.6}; 
		   net.setNodeDefinition("Forecast", aForecastDef);
		   
		   // Changing the nodes' spacial and visual attributes:
		   net.setNodePosition("Success", 20, 20, 80, 30);
		   net.setNodeBgColor("Success", Color.red);
		   net.setNodeTextColor("Success", Color.white);
		   net.setNodeBorderColor("Success", Color.black);
		   net.setNodeBorderWidth("Success", 2);
		   net.setNodePosition("Forecast", 30, 100, 60, 30);
		   
		   // Writting the network to a file:
		   net.writeFile("tutorial_a.xdsl");
		 }
		 catch (SMILEException e) {
		   System.out.println(e.getMessage());
		 }
		}
	public static void InfereceWithBayesianNetwork() {
		 try {
		   Network net = new Network();
		   net.readFile("tutorial_a.xdsl"); 		       
		   // ---- We want to compute P("Forecast" = Moderate) ----
		   // Updating the network:
		   net.updateBeliefs();
		   
		   // Getting the handle of the node "Forecast":
		   net.getNode("Forecast");
		   
		   // Getting the index of the "Moderate" outcome:
		   String[] aForecastOutcomeIds = net.getOutcomeIds("Forecast");
		   int outcomeIndex;
		   for (outcomeIndex = 0; outcomeIndex < aForecastOutcomeIds.length; outcomeIndex++)
		     if ("Moderate".equals(aForecastOutcomeIds[outcomeIndex]))
		       break;
		   
		   // Getting the value of the probability:
		   double[] aValues = net.getNodeValue("Forecast");
		   double P_ForecastIsModerate = aValues[outcomeIndex];
		   
		   System.out.println("P(\"Forecast\" = Moderate) = "      + P_ForecastIsModerate);
		   
		   
		   // ---- We want to compute P("Success" = Failure | "Forecast" = Good) ----
		   // Introducing the evidence in node "Forecast":
		   net.setEvidence("Forecast", "Good");
		   
		   // Updating the network:
		   net.updateBeliefs();
		   
		   // Getting the handle of the node "Success":
		   net.getNode("Success");
		   
		   // Getting the index of the "Failure" outcome:
		   String[] aSuccessOutcomeIds = net.getOutcomeIds("Success");
		   for (outcomeIndex = 0; outcomeIndex < aSuccessOutcomeIds.length; outcomeIndex++)
		     if ("Failure".equals(aSuccessOutcomeIds[outcomeIndex]))
		       break;
		   
		   // Getting the value of the probability:
		   aValues = net.getNodeValue("Success");
		   double P_SuccIsFailGivenForeIsGood = aValues[outcomeIndex];
		   
		   System.out.println("P(\"Success\" = Failure | \"Forecast\" = Good) = " + P_SuccIsFailGivenForeIsGood);
		   
		   // ---- We want to compute P("Success" = Success | "Forecast" = Poor) ----
		   // Clearing the evidence in node "Forecast":
		   net.clearEvidence("Forecast");
		   
		   // Introducing the evidence in node "Forecast":
		   net.setEvidence("Forecast", "Poor");
		   
		   // Updating the network:
		   net.updateBeliefs();
		   
		   // Getting the index of the "Failure" outcome:
		   aSuccessOutcomeIds = net.getOutcomeIds("Success");
		   for (outcomeIndex = 0; outcomeIndex < aSuccessOutcomeIds.length; outcomeIndex++)
		     if ("Success".equals(aSuccessOutcomeIds[outcomeIndex]))
		       break;
		   
		   // Getting the value of the probability:
		   aValues = net.getNodeValue("Success");
		   double P_SuccIsSuccGivenForeIsPoor = aValues[outcomeIndex];
		   
		   System.out.println("P(\"Success\" = Success | \"Forecast\" = Poor) = " + P_SuccIsSuccGivenForeIsPoor);
		 }
		 catch (SMILEException e) {
		   System.out.println(e.getMessage()); 
		 }
		}
	public static void UpgradeToInfluenceDiagram() {
		 try {
		   Network net = new Network();
		   net.readFile("tutorial_a.xdsl");
		   
		   // Creating node "Invest" and setting/adding outcomes:
		   net.addNode(Network.NodeType.List, "Invest");
		   net.setOutcomeId("Invest", 0, "Invest");
		   net.setOutcomeId("Invest", 1, "DoNotInvest");
		   
		   // Creating the value node "Gain" b:
		   net.addNode(Network.NodeType.Table, "Gain");
		   
		   // Adding an arc from "Invest" to "Gain":
		   net.addArc("Invest", "Gain");
		   
		   // Adding an arc from "Success" to "Gain":
		   net.getNode("Success");
		   net.addArc("Success", "Gain");
		   
		   // Filling in the utilities for the node "Gain". The utilities are:
		   // U("Invest" = Invest, "Success" = Success) = 10000
		   // U("Invest" = Invest, "Success" = Failure) = -5000
		   // U("Invest" = DoNotInvest, "Success" = Success) = 500
		   // U("Invest" = DoNotInvest, "Success" = Failure) = 500
		   double[] aGainDef = {10000, -5000, 500, 500};
		   net.setNodeDefinition("Gain", aGainDef);
		   
		   net.writeFile("tutorial_b.xdsl");
		 }
		 catch (SMILEException e) {
		   System.out.println(e.getMessage());
		 }
		}
	public static void InferenceWithInfluenceDiagram() {
		 try {
		   // Loading and updating the influence diagram: 
		   Network net = new Network();
		   net.readFile("tutorial_b.xdsl");
		   net.updateBeliefs();
		   
		   // Getting the utility node's handle:
		   net.getNode("Gain");
		   
		   // Getting the handle and the name of value indexing parent (decision node):
		   int[] aValueIndexingParents = net.getValueIndexingParents("Gain");
		   int nodeDecision = aValueIndexingParents[0];
		   String decisionName = net.getNodeName(nodeDecision);
		   
		   // Displaying the possible expected values:
		   System.out.println("These are the expected utilities:");
		   for (int i = 0; i < net.getOutcomeCount(nodeDecision); i++) {
		     String parentOutcomeId = net.getOutcomeId(nodeDecision, i);
		     double expectedUtility = net.getNodeValue("Gain")[i];
		     
		     System.out.print("  - \"" + decisionName + "\" = " + parentOutcomeId + ": ");
		     System.out.println("Expected Utility = " + expectedUtility);
		   }
		 }
		 catch (SMILEException e) {
		   System.out.println(e.getMessage());
		 }
		}
	public static void ComputeValueOfInformation() {
		 try {
		   Network net = new Network();
		   net.readFile("tutorial_b.xdsl");
		   
		   ValueOfInfo voi = new ValueOfInfo(net);
		   
		   // Getting the handles of nodes "Forecast" and "Invest":
		   net.getNode("Forecast");
		   net.getNode("Invest");
		   
		   voi.addNode("Forecast");
		   voi.setDecision("Invest");
		   voi.update();
		   
		   double[] results = voi.getValues();
		   double EVIForecast = results[0];
		   
		   System.out.println("Expected Value of Information (\"Forecast\") = " + EVIForecast);
		 }
		 catch (SMILEException e) {
		   System.out.println(e.getMessage());
		 }
		}
	public static void BNTFuzzyTaskModel(FuzzyTaskInput ftInput,FuzzyTaskOutput ftOutput) {
		try{
			new smile.License(
		
				"SMILE LICENSE 19a56b7c 42d4a244 2e297190 " +
				"THIS IS AN ACADEMIC LICENSE AND CAN BE USED " +
				"SOLELY FOR ACADEMIC RESEARCH AND TEACHING, " +
				"AS DEFINED IN THE BAYESFUSION ACADEMIC " +
				"SOFTWARE LICENSING AGREEMENT. " +
				"Serial #: 4q3gf60v0ja5hobk3svppgr8b " +
				"Issued for: Lin xin (papaya961128@gmail.com) " +
				"Academic institution: China University of Geography " +
				"Valid until: 2018-10-20 " +
				"Issued by BayesFusion activation server",
				new byte[] {
				24,127,-18,-22,106,104,70,-11,71,-92,-128,26,3,-46,-25,-50,
				17,-14,-39,112,75,55,8,-81,63,101,-108,-21,-14,110,-83,38,
				12,74,-68,57,-58,27,87,51,-84,-91,12,69,-36,-59,32,43,
				35,-32,11,0,-10,-17,-13,91,23,-40,6,65,-85,23,116,49
				}
			); 
		 Network net = new Network();
		 net.readFile("FuzzyTaskModel.xdsl");
		 net.clearAllEvidence();
		 net.updateBeliefs();
		 net.setEvidence("SolarRadiation", ftInput.getSolarRadiation());
		 net.setEvidence("CloudCover", ftInput.getCloudCover());
		 net.setEvidence("Topography", ftInput.getTopography());
		 net.setEvidence("FloodResponseLevel", ftInput.getFloodResponseLevel());
		 net.setEvidence("FloodStage", ftInput.getFloodStage());
		 net.setEvidence("FloodType", ftInput.getFloodType());
		 net.updateBeliefs();
		 ftOutput.setRadiationResolution(net.getNodeValue("RadiationResolution"));
		 ftOutput.setAccurateObservedTimeScale(net.getNodeValue("AccurateObservedTimeScale"));
		 ftOutput.setAccurateObservedSpaceScale(net.getNodeValue("AccurateObservedSpaceScale"));
		 ftOutput.setRevisitCycle(net.getNodeValue("RevisitCycle"));
		 ftOutput.setSpaceResolution(net.getNodeValue("SpaceResolution"));
		 ftOutput.setBandType(net.getNodeValue("BandType"));
		 //打印贝叶斯网络推理结果
		 printFuzzyTaskOutput(ftOutput);
				 
		 }
		 catch (SMILEException e) {
			   System.out.println(e.getMessage()); 
			 }
		
	}
	public static void printFuzzyTaskOutput(FuzzyTaskOutput fTaskOutput) {
		System.out.println("value of RadiationResolution:");
		for(int i=0;i<fTaskOutput.getRadiationResolution().length;i++) {
			System.out.print(fTaskOutput.getRadiationResolution()[i]+",");
		}
		System.out.print(";"+"\n");
		
		System.out.println("value of RevisitCycle:");
		for(int i=0;i<fTaskOutput.getRevisitCycle().length;i++) {
			System.out.print(fTaskOutput.getRevisitCycle()[i]+",");
		}
		System.out.print(";"+"\n");
		System.out.println("value of SpaceResolution:");
		for(int i=0;i<fTaskOutput.getSpaceResolution().length;i++) {
			System.out.print(fTaskOutput.getSpaceResolution()[i]+",");
		}
		System.out.print(";"+"\n");
		System.out.println("value of BandType:");
		for(int i=0;i<fTaskOutput.getBandType().length;i++) {
			System.out.print(fTaskOutput.getBandType()[i]+",");
		}
		System.out.print(";"+"\n");
		System.out.println("value of AccurateObservedTimeScale:");
		for(int i=0;i<fTaskOutput.getAccurateObservedTimeScale().length;i++) {
			System.out.print(fTaskOutput.getAccurateObservedTimeScale()[i]+",");
		}
		System.out.print(";"+"\n");
		System.out.println("value of AccurateObservedSpaceScale:");
		for(int i=0;i<fTaskOutput.getAccurateObservedSpaceScale().length;i++) {
			System.out.print(fTaskOutput.getAccurateObservedSpaceScale()[i]+",");
		}
		System.out.print(";"+"\n");
	}
	public static void main(String[] args) {
			
//			   CreateNetwork();
//			   InfereceWithBayesianNetwork();
//			   UpgradeToInfluenceDiagram();
//			   InferenceWithInfluenceDiagram();
//			   ComputeValueOfInformation();
		BNTFuzzyTaskModel(new FuzzyTaskInput(),new FuzzyTaskOutput());
			  
		}
}
