package de.wieczorek.rss.advisor.business;

import java.util.List;

import de.wieczorek.rss.advisor.types.rss.SentimentAtTime;

public class NetInputItem {
    private List<DeltaChartEntry> inputChartEntry;
    private SentimentAtTime inputSentiment;

    private int outputSentiment;

    public List<DeltaChartEntry> getInputChartEntry() {
	return inputChartEntry;
    }

    public void setInputChartEntry(List<DeltaChartEntry> inputChartEntry) {
	this.inputChartEntry = inputChartEntry;
    }

    public SentimentAtTime getInputSentiment() {
	return inputSentiment;
    }

    public void setInputSentiment(SentimentAtTime inputSentiment) {
	this.inputSentiment = inputSentiment;
    }

    public int getOutputSentiment() {
	return outputSentiment;
    }

    public void setOutputSentiment(int outputSentiment) {
	this.outputSentiment = outputSentiment;
    }

}