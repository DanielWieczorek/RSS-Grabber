package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;

public class DefaultOracle implements Oracle {

    private OracleConfiguration configuration;

    private double lastBuyPrice = -1.0;



    public DefaultOracle (OracleConfiguration configuration) {
       this.configuration = configuration;
    }

    @Override
    public ActionVertexType nextAction(StateEdge snapshot) {
        boolean canSell = snapshot.getAccount().getBtc() > 0;
        boolean canBuy = snapshot.getAccount().getEur() > 0;

        double currentPrice;
        currentPrice = snapshot.getAllStateParts().get(snapshot.getPartsEndIndex()).getChartEntry().getClose();

        int end = snapshot.getPartsEndIndex();

        int start = Math.max(0,end - configuration.getAverageTime());

        int averageNumbers = 0;
        double average =0;
        for(int i=start;i<end;i++) {
            if(snapshot.getAllStateParts().get(i).getMetricsSentiment() != null){
                averageNumbers++;
                average += snapshot.getAllStateParts().get(i).getMetricsSentiment().getPrediction();
            }
        }

        average /= (double)averageNumbers;

        if(canSell && lastBuyPrice > -1.0 &&  configuration.isStopLossActivated()){ // stop loss
            if(lastBuyPrice < currentPrice - configuration.getStopLossThreshold()){
                lastBuyPrice = -1.0;
                return ActionVertexType.SELL;
            }
        }


        if(canBuy){
            if(compare(average,configuration.getBuyThreshold(),configuration.getBuyComparison())){
                lastBuyPrice = currentPrice;
                return ActionVertexType.BUY;
            }
            else {
                return ActionVertexType.SELL; // do nothing
            }

        }
        else {

            if(compare(average, configuration.getSellThreshold(),configuration.getSellComparison())){
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
