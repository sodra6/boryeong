package com.mindone.Boryeongapi.domain.entity.main;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@Table(name="SETTING")
public class Setting {

    @Id
    @ApiModelProperty(value = "ID")
    @Column(name="ID")
    private String id;

    @ApiModelProperty(value="종류")
    @Column(name="KIND")
    private String kind;

    @ApiModelProperty(value="방향")
    @Column(name="LINE")
    private String line;

    @ApiModelProperty(value="이름")
    @Column(name="NAME")
    private String name;

    @ApiModelProperty(value="관심값")
    @Column(name="WARNING_VAL")
    private double warningVal;

    @ApiModelProperty(value="이상값")
    @Column(name="DANGER_VAL")
    private double dangerVal;

    @ApiModelProperty(value="생성날짜")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @Column(name="CRET_DT")
    private Date cretDt;

    @ApiModelProperty(value="수정날짜")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @Column(name="UPDT_DT")
    private Date updtDt;

    @ApiModelProperty(value="순서")
    @Column(name="SORT")
    private int sort;

    @Builder
    public Setting(String id, String kind, String line, String name, double warningVal, double dangerVal, Date cretDt, Date updtDt, int sort) {
        this.id = id;
        this.kind = kind;
        this.line = line;
        this.name = name;
        this.warningVal = warningVal;
        this.dangerVal = dangerVal;
        this.cretDt = cretDt;
        this.updtDt = updtDt;
        this.sort = sort;
    }
}
