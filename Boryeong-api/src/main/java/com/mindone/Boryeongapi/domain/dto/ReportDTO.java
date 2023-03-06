package com.mindone.Boryeongapi.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ReportDTO {
    public String id;
    public Date logTime;
    public String status;

    @Override
    public String toString() {
        return "ReportDTO{" +
                "id='" + id + '\'' +
                ", logTime=" + logTime +
                ", status='" + status + '\'' +
                '}';
    }
}
