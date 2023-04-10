package com.nice.avishkar;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * Basic test cases for candidates
 * */
public class SolutionTests {

	private static ElectionResult resultData;

	// Run once, execute solution and load resultData
	@BeforeClass
	public static void initAndExecute() throws FileNotFoundException {
		Instant start = Instant.now();
		Path candidateFilePath = Paths.get("src/main/resources/candidateFile.csv");
		Path votingFilePath = Paths.get("src/main/resources/votingFile.csv");
		Solution solution = new Solution();

		resultData = solution.execute(candidateFilePath, votingFilePath);
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		System.err.println("Execution took "+ timeElapsed + " millis");
	}


	@Test
	public void test1() {

		resultData.getResultData().stream().forEach(constituencyResult -> {
			switch (constituencyResult.getConstituencyName()){
				case "Ulhasnagar":
					Assert.assertTrue(constituencyResult.getWinnerName().equals("AnamikaPilla"));
					Assert.assertTrue(constituencyResult.getCandidateList().get(0).getCandidateName().equals("AnamikaPilla"));
					Assert.assertTrue(constituencyResult.getCandidateList().get(0).getVotes()==155);
					Assert.assertTrue(constituencyResult.getCandidateList().get(1).getCandidateName().equals("BhavesChaturvedi"));
					Assert.assertTrue(constituencyResult.getCandidateList().get(1).getVotes()==149);
					Assert.assertTrue(constituencyResult.getCandidateList().get(2).getCandidateName().equals("SwaraIyer"));
					Assert.assertTrue(constituencyResult.getCandidateList().get(2).getVotes()==145);
					break;
				case "Niphad":
					Assert.assertTrue(constituencyResult.getWinnerName().equals("NO_WINNER"));
					Assert.assertTrue(constituencyResult.getCandidateList().get(0).getCandidateName().equals("KamaVarma"));
					Assert.assertTrue(constituencyResult.getCandidateList().get(0).getVotes()==270);
					break;
				case "Hingna":
					Assert.assertTrue(constituencyResult.getWinnerName().equals("HiranyaKhatri"));
					Assert.assertTrue(constituencyResult.getCandidateList().get(0).getCandidateName().equals("HiranyaKhatri"));
					Assert.assertTrue(constituencyResult.getCandidateList().get(0).getVotes()==256);
					break;
			}
		});

	}

}
