package com.nice.avishkar;

import java.util.List;

public class ElectionResult {

	private List<ConstituencyResult> resultData;

	public ElectionResult() {
		super();
	}

	public ElectionResult(List<ConstituencyResult> resultData) {
		super();
		this.resultData = resultData;
	}

	public List<ConstituencyResult> getResultData() {
		return resultData;
	}

	public void setResultData(List<ConstituencyResult> resultData) {
		this.resultData = resultData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ElectionResult [resultData=");
		builder.append(resultData);
		builder.append("]");
		return builder.toString();
	}

}
