package com.mindone.Boryeongapi.domain.entity.main.key;

import java.io.Serializable;
import java.util.Date;

public class TagmapKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String tagId;

    public TagmapKey(){

    }

    public TagmapKey(String id, String tagId) {
        this.id = id;
        this.tagId = tagId;
    }
}
