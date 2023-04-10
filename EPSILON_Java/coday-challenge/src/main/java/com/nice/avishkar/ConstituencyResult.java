package com.nice.avishkar;

import java.util.ArrayList;
import java.util.List;

public class ConstituencyResult {

	private String constituencyName;
	private String winnerName;
	private List<CandidateVotes> candidateList;
	
	public ConstituencyResult() {
		super();
	}

	public ConstituencyResult(String constituencyName, String winnerName, List<CandidateVotes> candidateList) {
		super();
		this.constituencyName = constituencyName;
		this.winnerName = winnerName;
		this.candidateList = candidateList;

	}

	public String getConstituencyName() {
		return this.constituencyName;
	}

	public void setConstituencyName(String constituencyName) {
		this.constituencyName = constituencyName;
	}

	public String getWinnerName() {
		return winnerName;
	}

	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}

	public List<CandidateVotes> getCandidateList() {
		return candidateList;
	}

	public void setCandidateList(List<CandidateVotes> candidateList) {
		this.candidateList = candidateList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConstituencyResult [constituencyName=");
		builder.append(constituencyName);
		builder.append(", winnerName=");
		builder.append(winnerName);
		builder.append(", candidateList=");
		builder.append(candidateList);
		builder.append("]");
		return builder.toString();
	}

}
