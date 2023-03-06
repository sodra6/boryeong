package com.mindone.Boryeongapi.domain.dto;

import lombok.Data;

@Data
public class TimeRangeDTO {

	public double todaySupply; //금일 공급량
	public double weekSupply; //주간 공급량
	public double monthSupply; //월간 공급량
	public double todayPress; //금일 평균수압
	public double weekPress; //주간 평균수압
	public double monthPress; //월간 평균수압
	public double depthNow; //현재 집수조 수심
	public double depth1h; //1시간전 집수조 수심
	public double depth2h; //2시간전 집수조 수심

	@Override
	public String toString() {
		return "TimeRangeDTO{" +
				"todaySuply=" + todaySupply +
				", weekSuply=" + weekSupply +
				", monthSuply=" + monthSupply +
				", todayPress=" + todayPress +
				", weekPress=" + weekPress +
				", monthPress=" + monthPress +
				", depthNow=" + depthNow +
				", depth1h=" + depth1h +
				", depth2h=" + depth2h +
				'}';
	}
}
