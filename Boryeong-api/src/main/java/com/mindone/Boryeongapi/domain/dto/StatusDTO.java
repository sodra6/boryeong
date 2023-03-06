package com.mindone.Boryeongapi.domain.dto;

import lombok.Data;

@Data
public class StatusDTO {

	public String brPress; //보령수압상태
	public String brValve; //보령밸브상태
	public String brTele; //보령통신상태
	public String wsPress; //원산도수압상태
	public String wsValve; //원산도밸브상태
	public String wsTele; //원산도통신상태
	public String flow; //유량상태
	public String depth; //수위상태

	@Override
	public String toString() {
		return "StatusDTO{" +
				"brPress='" + brPress + '\'' +
				", brValve='" + brValve + '\'' +
				", brTele='" + brTele + '\'' +
				", wsPress='" + wsPress + '\'' +
				", wsValve='" + wsValve + '\'' +
				", wsTele='" + wsTele + '\'' +
				", flow='" + flow + '\'' +
				", depth='" + depth + '\'' +
				'}';
	}
}
