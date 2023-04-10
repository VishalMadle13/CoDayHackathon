package com.nice.avishkar;

import java.util.*;

public class CandidateVotes {
	private String  candidateName;
	private long votes;
	private Set<String> voters;
	private Set<String> booths;
	private Map<String, String> VoterBoothUpair = new HashMap<>();
	public CandidateVotes() {
		super();
	}

	public CandidateVotes(String candidateName, long votes) {
		super();
		this.candidateName = candidateName;
		this.votes = votes;
		this.voters = new HashSet<>();
		this.booths = new HashSet<>();
		this.VoterBoothUpair = new HashMap<>();
	}

	public void setVoters(Set<String> voters) {
		this.voters = voters;
	}
	public void setBooths(Set<String> booths) {
		this.booths = booths;
	}
	public void setVoterBoothUpair(Map<String,String> VoterBoothUpair) {
		this.VoterBoothUpair = VoterBoothUpair;
	}


	public Set<String> getVoters() {
		return voters;
	}
	public Set<String> getBooths() {
		return booths;
	}
	public Map<String,String> getVoterBoothUpair() {
		return VoterBoothUpair;
	}


	public void addVoter(String voter){
		voters.add(voter);
	}
	public void addBooth(String booth){
		booths.add(booth);
	}

	public void addVoterBoothUpair(String voter,String booth){VoterBoothUpair.put(voter,booth);}


	public void incrementVote(){
		votes++;
	}
	public void decrementVote(){
		votes--;
	}

	public String getCandidateName() {
		return candidateName;
	}

	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}

	public long getVotes() {
		return votes;
	}
	public void setVotes(long votes) {
		this.votes = votes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(candidateName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CandidateVotes other = (CandidateVotes) obj;
		return Objects.equals(candidateName, other.candidateName);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CandidateVotes [candidateName=");
		builder.append(candidateName);
		builder.append(", votes=");
		builder.append(votes);
		builder.append("]");
		return builder.toString();
	}


	
	
}
