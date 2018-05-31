package com.weyao.srv.report.entity;

import java.io.Serializable;

/**
 * @ClassName: AngleRecordVO
 * @Description: 组合维度记录值视图对象
 * @author taoxiaoyan
 * @date 2016年11月02日 下午7:17:06
 * @version [1.0, 2016年11月02日]
 * @since version 1.0
 */
public class CombinationAngleVO implements Serializable {

	private static final long serialVersionUID = -3912418573723458808L;

	/**
	 * @Fields angleName : 维度名1
	 */
	private String angleNameFirst;
	/**
	 * @Fields angleName : 维度名2
	 */
	private String angleNameSecond;
	/**
	 * @Fields angleVal : 维度值
	 */
	private Object angleVal;

	public String getAngleNameFirst() {
		return angleNameFirst;
	}

	public void setAngleNameFirst(String angleNameFirst) {
		this.angleNameFirst = angleNameFirst;
	}

	public String getAngleNameSecond() {
		return angleNameSecond;
	}

	public void setAngleNameSecond(String angleNameSecond) {
		this.angleNameSecond = angleNameSecond;
	}

	public Object getAngleVal() {
		return angleVal;
	}

	public void setAngleVal(Object angleVal) {
		this.angleVal = angleVal;
	}

}
