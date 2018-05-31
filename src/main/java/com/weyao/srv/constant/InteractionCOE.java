package com.weyao.srv.constant;

/**
 * @ClassName: InteractionCOE
 * @Description: 交互量系数值
 * @author Tao xiaoyan
 * @date 2017年8月1日 上午11:28:24
 * @version [1.0, 2017年8月1日]
 * @since version 1.0
 */
public enum InteractionCOE {

	算价来源权值_又一单(6), 
	算价来源权值_经管(10), 
	算价来源权值_量子(3), 
	新增订单数_来源又一单或经管或者量子(2), 
	完成的订单数(11);

	private int value;

	public final int getValue() {
		return value;
	}

	public final void setValue(int value) {
		this.value = value;
	}

	private InteractionCOE(int value) {
		this.value = value;
	}

}
