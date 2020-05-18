package de.wieczorek.rss.trading.types;

public class StateEdgeChainMetaInfo {

    /**
     * offset of the first state edge
     */
    private int offset;

    /**
     * number of state edges
     */
    private int depth;

    /**
     * chart entries per state edge
     */
    private int width;

    /**
     * offset between state edges in minutes
     */
    private int stepping;

    public int getStepping() {
        return stepping;
    }

    public void setStepping(int stepping) {
        this.stepping = stepping;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
