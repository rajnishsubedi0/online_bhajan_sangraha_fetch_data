package com.rkant.bhajanapp.FirstActivities;

public class DataHolderForDB {
     String BHAJAN_ID;
    String BHAJAN_NAME_NEPALI;
    String BHAJAN_NAME_ENGLISH;
    public DataHolderForDB(String BHAJAN_NAME_NEPALI, String BHAJAN_NAME_ENGLISH,String BHAJAN_ID){
        this.BHAJAN_ID=BHAJAN_ID;
        this.BHAJAN_NAME_ENGLISH=BHAJAN_NAME_ENGLISH;
        this.BHAJAN_NAME_NEPALI=BHAJAN_NAME_NEPALI;
    }

    public String getBHAJAN_NAME_ENGLISH() {
        return BHAJAN_NAME_ENGLISH;
    }

    public String getBHAJAN_NAME_NEPALI() {
        return BHAJAN_NAME_NEPALI;
    }
    public String getBHAJAN_ID() {
        return BHAJAN_ID;
    }



}
