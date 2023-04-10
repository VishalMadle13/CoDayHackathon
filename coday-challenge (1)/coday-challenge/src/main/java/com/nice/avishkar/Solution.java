package com.nice.avishkar;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.MalformedParameterizedTypeException;
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
			String candidateName = values[1].trim();

			if(constituencyCandidates.containsKey(constituency)){
				List<String> candidateNames = new ArrayList<>();
				candidateNames = constituencyCandidates.get(constituency);
				candidateNames.add(candidateName);
				constituencyCandidates.put(constituency, candidateNames);
			} else{
				List<String> candidateNames = new ArrayList<>();
				candidateNames.add(candidateName);
				constituencyCandidates.put(constituency, candidateNames);
			}
		}
		scanner.close();

		return constituencyCandidates;
	}

	public void updateVoteCount(Path votingFile, Map<String, List<String>> constituencyCandidate, Map<String, ConstituencyResult> resultMap) throws FileNotFoundException{
		Scanner scanner = new Scanner(votingFile.toFile());

		while (scanner.hasNextLine()){
			String entry = scanner.nextLine();
			String[] values = entry.split(",");
			String voter = values[0].trim();
			String constituency = values[1].trim();
			String votingBooth = values[2].trim();
			String candidate = values[3].trim();

			boolean voterHasVoted = false;
			ConstituencyResult constituencyResult = resultMap.get(constituency);

//			for(CandidateVotes candidateVotes: constituencyResult.getCandidateList()){
//				if (candidateVotes.getBooths().contains(votingBooth) && candidateVotes.getVoters().contains(voter)) {
//					voterHasVoted = true;
//					candidateVotes.getVoters().remove(voter);
//
//					//*//
//				}
//			}
			for(CandidateVotes candidateVotes: constituencyResult.getCandidateList()){
				if (candidateVotes.getVoterBoothUpair().containsKey(voter)) {
					voterHasVoted = true;
					candidateVotes.decrementVote();
//					candidateVotes.decrementVote();
				}
			}
			if(voterHasVoted){
				continue;
			}

			//Check for candidate
			List<String> candidateList = constituencyCandidate.get(constituency);
			if(!candidateList.contains(candidate)){
				continue;
			}

			//Update votes for the candidate
			for(CandidateVotes candidateVotes: constituencyResult.getCandidateList()){
				if(candidateVotes.getCandidateName().equals(candidate)){
					candidateVotes.incrementVote();
					candidateVotes.addBooth(votingBooth);
					candidateVotes.addVoter(voter);
					candidateVotes.addVoterBoothUpair(voter,votingBooth);
					break;
				}
			}
		}
		scanner.close();

		//Update Winner
		updateWinner(resultMap);
	}

	public void updateWinner(Map<String, ConstituencyResult> resultMap){
		for(ConstituencyResult constituencyResult: resultMap.values()){
			List<CandidateVotes> candidateVotesList = constituencyResult.getCandidateList();
			CandidateVotes nota = null;
			CandidateVotes winner = candidateVotesList.get(0);
			CandidateVotes secondHighest = null;
			boolean isTie = false;

			Set<String> votedBooths = new HashSet<>();
			Map<String,String> VoterBoothUpair = new HashMap<>();

			for(CandidateVotes candidateVotes: candidateVotesList){
				if(candidateVotes.getCandidateName().equals("NOTA")){
					nota = candidateVotes;
					continue;
				}

				if( candidateVotes.getVotes() > winner.getVotes()){
						secondHighest = winner;
						winner = candidateVotes;
						isTie = false;

				}
				else if(secondHighest == null || candidateVotes.getVotes() > secondHighest.getVotes()) {
					secondHighest = candidateVotes;
					isTie = candidateVotes.getVotes() == winner.getVotes();
				} else if (candidateVotes.getVotes() == winner.getVotes()) {
					isTie = true;
				}

				Set<String> candidateBooths = candidateVotes.getBooths();
//				if(!Collections.disjoint(votedBooths, candidateBooths)){
//					throw new IllegalStateException("Voter has voted in multiple constituencies" + constituencyResult.getWinnerName());
//				}
				votedBooths.addAll(candidateBooths);
			}
			if(isTie || winner == null || nota.getVotes() > winner.getVotes()){
				constituencyResult.setWinnerName("NO_WINNER");
			} else if(nota.getVotes() == winner.getVotes()) {
				constituencyResult.setWinnerName(secondHighest.getCandidateName());
			} else {
				constituencyResult.setWinnerName(winner.getCandidateName());
			}
		}
	}


	public ElectionResult execute(Path candidateFile, Path votingFile) throws FileNotFoundException {
		ElectionResult resultData = new ElectionResult(new ArrayList<>());


		// **********************   Read CSV candidateFile and Store in Map  ************************************
		//                        { key : value } ==> { <constituency> : [<candidate>] }
		Map<String, List<String>> constituencyCandidates = readCandidateFile(candidateFile);

		// printing Map constituencyCandidates
//		for (String constituency : constituencyCandidates.keySet()) {
//
//			// Print the constituency name
//			System.out.println("Constituency: " + constituency);
//
//			// Get the list of candidates for the current constituency
//			List<String> candidates = constituencyCandidates.get(constituency);
//
//			// Iterate over the list of candidates and print each one
//			for (String candidate : candidates) {
//				System.out.println("- " + candidate);
//			}
//		}


		// **********************   Initialisation of values of resultData  *************************************
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


		// **********************  Creating Map Stores Result of Each Constituency  *************************************
		Map<String, ConstituencyResult> resultMap = new HashMap<>();
		for(ConstituencyResult constituencyResult: resultData.getResultData()){
			resultMap.put(constituencyResult.getConstituencyName(), constituencyResult);
		}

		// **********************  Updating VoteCount  *************************************
		for(String constituency: constituencyCandidates.keySet()){
//			System.out.println(constituency);
			updateVoteCount(votingFile, constituencyCandidates, resultMap);
		}

		updateWinner(resultMap);

    	return resultData;
	}

}