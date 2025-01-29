package com.rkant.bhajanapp.FirstActivities;

public class DataHolderForActualBhajan {
    String id,bhajans;
    public DataHolderForActualBhajan(String id, String bhajans){
        this.id=id;
        this.bhajans=bhajans;
    }

    public String getId() {
        return id;
    }

    public String getBhajans() {
        return bhajans;
    }
}
