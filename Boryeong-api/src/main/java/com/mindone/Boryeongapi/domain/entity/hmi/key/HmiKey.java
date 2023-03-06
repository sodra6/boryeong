package com.mindone.Boryeongapi.domain.entity.hmi.key;

import java.io.Serializable;
import java.util.Date;

public class HmiKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tagId;
    private Date logTime;

    public HmiKey(){

    }

    public HmiKey(String tagId, Date logTime) {
        this.tagId = tagId;
        this.logTime = logTime;
    }

}
