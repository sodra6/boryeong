package com.mindone.Boryeongapi.domain.entity.hmi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindone.Boryeongapi.domain.entity.hmi.key.HmiKey;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@IdClass(HmiKey.class)
@Table(name="HMI")
public class Hmi implements Serializable {

        @Id
        @ApiModelProperty(value = "태그ID")
        @Column(name="TAG_ID")
        private String tagId;

        @ApiModelProperty(value="값")
        @Column(name="VAL")
        private String val;

        @ApiModelProperty(value="비고")
        @Column(name="ETC")
        private String etc;

        @Id
        @ApiModelProperty(value="수집시간")
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
        @Column(name="LOG_TIME")
        private Date logTime;

        @Builder
        public Hmi(String tagId, String val, String etc, Date logTime) {
            this.tagId = tagId;
            this.val = val;
            this.etc = etc;
            this.logTime = logTime;
        }
}

