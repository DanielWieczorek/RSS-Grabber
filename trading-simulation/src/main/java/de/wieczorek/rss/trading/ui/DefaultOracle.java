package de.wieczorek.rss.trading.ui;

import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultOracle implements Oracle {
    @Override
    public ActionVertexType nextAction(StateEdge snapshot) {
        boolean canSell = snapshot.getAccount().getBtc() > 0;
        boolean canBuy = snapshot.getAccount().getEur() > 0;



        int end = snapshot.getPartsEndIndex();

        int start = Math.max(0,end -10);

        int averageNumbers = 0;
        double average =0;
        for(int i=start;i<end;i++) {
            if(snapshot.getAllStateParts().get(i).getMetricsSentiment() != null){
                averageNumbers++;
                average += snapshot.getAllStateParts().get(i).getMetricsSentiment().getPrediction();
            }
        }

        average /= (double)averageNumbers;

        if(canBuy){
            if(average > 15){
                return ActionVertexType.BUY;
            }
            else {
                return ActionVertexType.SELL;
            }

        }
        else {

            if(average <= -0.0){
                return ActionVertexType.SELL;
            } else {
                return ActionVertexType.BUY;
            }

        }
    }
}
