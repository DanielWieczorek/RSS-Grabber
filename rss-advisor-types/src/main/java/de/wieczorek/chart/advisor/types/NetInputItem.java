package de.wieczorek.chart.advisor.types;

import java.util.List;

import de.wieczorek.rss.insight.types.SentimentAtTime;

public class NetInputItem {
    private List<DeltaChartEntry> inputChartEntry;
    private SentimentAtTime inputSentiment;

    private double outputDelta;

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

    public double getOutputDelta() {
	return outputDelta;
    }

    public void setOutputDelta(double outputDelta) {
	this.outputDelta = outputDelta;
    }

}