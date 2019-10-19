package de.wieczorek.rss.trading.common;

import de.wieczorek.rss.trading.common.Oracle;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;

import javax.enterprise.context.ApplicationScoped;
import java.util.Comparator;

public class DefaultOracle implements Oracle {

    private int sellThreshold;
    private int buyThreshold;
    private int averageTime;

    private Comparison buyComparison = Comparison.GREATER;
    private Comparison sellComparison =  Comparison.LOWER;

    private boolean isStopLossActivated = false;

    private int stopLossThreshold = 0;

    private double lastBuyPrice = -1.0;



    public DefaultOracle (int sellThreshold, int buyThreshold, int averageTime) {
        this.sellThreshold = sellThreshold;
        this.buyThreshold = buyThreshold;
        this.averageTime = averageTime;
    }

    public DefaultOracle (int sellThreshold, int buyThreshold, int averageTime, Comparison buyComparison ,Comparison sellComparison) {
        this.sellThreshold = sellThreshold;
        this.buyThreshold = buyThreshold;
        this.averageTime = averageTime;
        this.buyComparison = buyComparison;
        this.sellComparison = sellComparison;
    }

    public DefaultOracle (int sellThreshold, int buyThreshold, int averageTime, Comparison buyComparison ,Comparison sellComparison,int stopLossThreshold ) {
        this.sellThreshold = sellThreshold;
        this.buyThreshold = buyThreshold;
        this.averageTime = averageTime;
        this.buyComparison = buyComparison;
        this.sellComparison = sellComparison;
        this.isStopLossActivated = true;
        this.stopLossThreshold = stopLossThreshold;
    }

    @Override
    public ActionVertexType nextAction(StateEdge snapshot) {
        boolean canSell = snapshot.getAccount().getBtc() > 0;
        boolean canBuy = snapshot.getAccount().getEur() > 0;

        double currentPrice;
    try {
         currentPrice = snapshot.getAllStateParts().get(snapshot.getPartsEndIndex()).getChartEntry().getClose();
    } catch (Exception e) {
        throw e;
    }
        int end = snapshot.getPartsEndIndex();

        int start = Math.max(0,end - averageTime);

        int averageNumbers = 0;
        double average =0;
        for(int i=start;i<end;i++) {
            if(snapshot.getAllStateParts().get(i).getMetricsSentiment() != null){
                averageNumbers++;
                average += snapshot.getAllStateParts().get(i).getMetricsSentiment().getPrediction();
            }
        }

        average /= (double)averageNumbers;

        if(canSell && lastBuyPrice > -1.0){
            if(lastBuyPrice < currentPrice - stopLossThreshold){
                lastBuyPrice = -1.0;
                return ActionVertexType.SELL;
            }
        }


        if(canBuy){
            if(average > buyThreshold){
                lastBuyPrice = currentPrice;
                return ActionVertexType.BUY;
            }
            else {
                return ActionVertexType.SELL; // do nothing
            }

        }
        else {

            if(average <= sellThreshold){
                lastBuyPrice = -1.0;
                return ActionVertexType.SELL;
            } else {
                return ActionVertexType.BUY; // do nothing
            }

        }
    }

    private boolean compare(double value, double threshold, Comparison comparison) {

        if(comparison == Comparison.GREATER) {
            return value > threshold;
        } else {
            return value < threshold;
        }

    }
}
