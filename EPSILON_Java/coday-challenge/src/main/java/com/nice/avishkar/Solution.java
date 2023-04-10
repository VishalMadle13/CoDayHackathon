package com.nice.avishkar;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;

public class Solution {

	public Map<String, List<String>> readCandidateFile(Path candidateFile) throws FileNotFoundException {
		Map<String, List<String>> constituencyCandidates = new HashMap<>();
		Scanner scanner = new Scanner(candidateFile.toFile());

		while(scanner.hasNextLine()){
			String entry = scanner.nextLine();
			String[] values = entry.split(",");

			String constituency = values[0].trim();
			List<String> candidateNames = constituencyCandidates.getOrDefault(constituency, new ArrayList<>());
			for(int i = 1; i<values.length; i++){
				String candidateName = values[i].trim();
					candidateNames.add(candidateName);
			}
			constituencyCandidates.put(constituency, candidateNames);
		}
		scanner.close();

		return constituencyCandidates;
	}

	public Map<String, ConstituencyResult> updateVoteCount(Path votingFile, Map<String, List<String>> constituencyCandidate, Map<String, ConstituencyResult> resultMap) throws FileNotFoundException{
		Scanner scanner = new Scanner(votingFile.toFile());
		int callCounter = 0;
		while (scanner.hasNextLine()){
			String entry = scanner.nextLine();
			String[] values = entry.split(",");
			String voter = values[0].trim();
			String constituency = values[1].trim();
			String votingBooth = values[2].trim();
			String candidate = values[3].trim();

			boolean voterHasVoted = false;
			ConstituencyResult constituencyResult = resultMap.get(constituency);
			for(CandidateVotes candidateVotes: constituencyResult.getCandidateList()){
				if (candidateVotes.getBooths().contains(votingBooth) && candidateVotes.getVoters().contains(voter)) {
					voterHasVoted = true;

					candidateVotes.getVoters().remove(voter);
				}
			}
			if(voterHasVoted){
				continue;
			}

			//Check for candidate
			List<String> candidateList = constituencyCandidate.get(constituency);
				if(candidateList != null && !candidateList.contains(candidate) && !candidate.equals("NOTA")){
					continue;
				}

			//Update votes for the candidate
			for(CandidateVotes candidateVotes: constituencyResult.getCandidateList()){
				if(candidateVotes.getCandidateName().equals(candidate)){
					candidateVotes.incrementVote();
					candidateVotes.addBooth(votingBooth);
					candidateVotes.addVoter(voter);
					break;
				}
			}
		}
		scanner.close();


		//Update Winner
		resultMap = updateWinner(resultMap);

		System.out.println("updateVoteCount called " + (++callCounter) + " times");

		return resultMap;
	}

	public Map<String,ConstituencyResult>updateWinner(Map<String, ConstituencyResult> resultMap) {
		for (ConstituencyResult constituencyResult : resultMap.values()) {
			List<CandidateVotes> candidateVotesList = constituencyResult.getCandidateList();
			CandidateVotes nota = null;
			CandidateVotes winner = null;
			CandidateVotes secondHighest = null;
			boolean isTie = false;

			Set<String> votedBooths = new HashSet<>();

			for (CandidateVotes candidateVotes : candidateVotesList) {
				if (candidateVotes.getCandidateName().equals("NOTA")) {
					nota = candidateVotes;
					continue;
				}

				if (winner == null || candidateVotes.getVotes() > winner.getVotes()) {
					secondHighest = winner;
					winner = candidateVotes;
					isTie = false;
				} else if (candidateVotes.getVotes() == winner.getVotes() && winner.getCandidateName() != "NOTA") {
					isTie = true;
				} else if (secondHighest == null || candidateVotes.getVotes() > secondHighest.getVotes()) {
					secondHighest = candidateVotes;
				}

				Set<String> candidateBooths = candidateVotes.getBooths();
//				if (!Collections.disjoint(votedBooths, candidateBooths)) {
//					throw new IllegalStateException("Voter has voted in multiple constituencies" + constituencyResult.getWinnerName());
//				}
				votedBooths.addAll(candidateBooths);
				if (winner.getCandidateName().equals("NOTA") && secondHighest != null) {
					winner = secondHighest;
					secondHighest = null;
				}
			}
			if (isTie) {
				constituencyResult.setWinnerName("NO_WINNER");
			} else if (winner.getCandidateName().equals("NOTA")) {
				constituencyResult.setWinnerName(secondHighest.getCandidateName());
			} else if (winner == null || nota.getVotes() > winner.getVotes()) {
				constituencyResult.setWinnerName("NO_WINNER");
			} else if (nota.getVotes() == winner.getVotes()) {
				constituencyResult.setWinnerName(winner.getCandidateName());
			} else {
				constituencyResult.setWinnerName(winner.getCandidateName());
			}
		}
		return resultMap;
	}







	public ElectionResult execute(Path candidateFile, Path votingFile) throws FileNotFoundException {
		ElectionResult resultData = new ElectionResult(new ArrayList<>());

		// Write code here to read CSV files and process them
		Map<String, List<String>> constituencyCandidates = readCandidateFile(candidateFile);
		for(String constituencyName: constituencyCandidates.keySet()){
			List<String> candidateNames = constituencyCandidates.get(constituencyName);
			List<CandidateVotes> candidateVotesList = new ArrayList<>();
			for (String candidateName : candidateNames) {
				if(constituencyCandidates.containsKey(candidateName)){
					continue;
				}
				CandidateVotes candidateVotes = new CandidateVotes(candidateName, 0);
				candidateVotesList.add(candidateVotes);
			}
			CandidateVotes nota = new CandidateVotes("NOTA", 0);
			candidateVotesList.add(nota);
			ConstituencyResult constituencyResult = new ConstituencyResult(constituencyName, null, candidateVotesList);
			resultData.getResultData().add(constituencyResult);
		}

		Map<String, ConstituencyResult> resultMap = new HashMap<>();
		for(ConstituencyResult constituencyResult: resultData.getResultData()){
			resultMap.put(constituencyResult.getConstituencyName(), constituencyResult);
		}

//		for(String constituency: constituencyCandidates.keySet()){
//			updateVoteCount(votingFile, constituencyCandidates, resultMap);
//		}

		resultMap = updateVoteCount(votingFile, constituencyCandidates, resultMap);

		resultMap = updateWinner(resultMap);

		for(String constituencyName : resultMap.keySet()){
			ConstituencyResult constituencyResult = resultMap.get(constituencyName);
			//Sort the candidate List
			Collections.sort(constituencyResult.getCandidateList(), new Comparator<CandidateVotes>() {
				@Override
				public int compare(CandidateVotes c1, CandidateVotes c2) {
					int result = Long.compare(c2.getVotes(), c1.getVotes());
					if (result == 0) {
						result = c1.getCandidateName().compareTo(c2.getCandidateName());
					}
					return result;
				}
			});
			resultData.getResultData().add(constituencyResult);
		}

		System.out.println(resultData);
		return resultData;
	}

}