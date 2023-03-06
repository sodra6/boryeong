package com.mindone.Boryeongapi.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusTmpDTO {

    public String id;
    public String line;
    public String kind;
    public String val;

    @Override
    public String toString() {
        return "StatusTmpDTO{" +
                "id='" + id + '\'' +
                ", line='" + line + '\'' +
                ", kind='" + kind + '\'' +
                ", val='" + val + '\'' +
                '}';
    }
}
