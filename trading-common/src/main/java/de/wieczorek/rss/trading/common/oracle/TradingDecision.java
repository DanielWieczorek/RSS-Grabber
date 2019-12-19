package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.ActionVertexType;

public class TradingDecision {
    private ActionVertexType decision;
    private DecisionReason reason;

    public TradingDecision(ActionVertexType decision, DecisionReason reason) {
        this.decision = decision;
        this.reason = reason;
    }

    public DecisionReason getReason() {
        return reason;
    }

    public void setReason(DecisionReason reason) {
        this.reason = reason;
    }

    public ActionVertexType getDecision() {
        return decision;
    }

    public void setDecision(ActionVertexType decision) {
        this.decision = decision;
    }
}
