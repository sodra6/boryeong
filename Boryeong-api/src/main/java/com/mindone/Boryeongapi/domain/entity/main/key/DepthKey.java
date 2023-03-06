package com.mindone.Boryeongapi.domain.entity.main.key;

import java.io.Serializable;
import java.util.Date;

public class DepthKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Date coltDt;

    public DepthKey(){

    }

    public DepthKey(String id, Date coltDt) {
        this.id = id;
        this.coltDt = coltDt;
    }
}
