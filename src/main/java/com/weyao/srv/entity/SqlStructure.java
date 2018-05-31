package com.weyao.srv.entity;



public class SqlStructure {
	
	public enum Symbol{
		AND(" AND "),
		OR(" OR ");
		private String symbol;

		private Symbol(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}
		
	}
	private StringBuffer selectSql;
	private StringBuffer whereSql;
	private String orderbySql;
	private String limitSql;
	private boolean isFirst = true;
	private String where = " WHERE ";
	
	public SqlStructure(StringBuffer selectSql) {
		super();
		this.selectSql = selectSql;
		whereSql = new StringBuffer(where);
	}

	public SqlStructure(StringBuffer selectSql, String orderbySql,
						String limitSql) {
		super();
		this.selectSql = selectSql;
		this.orderbySql = orderbySql;
		this.limitSql = limitSql;
		whereSql = new StringBuffer(where);
	}

	public SqlStructure(StringBuffer selectSql, StringBuffer whereSql,
						String orderbySql, String limitSql) {
		super();
		this.selectSql = selectSql;
		this.whereSql = whereSql;
		isFirst = false;
		this.orderbySql = orderbySql;
		this.limitSql = limitSql;
	}
	
	public SqlStructure(StringBuffer selectSql, StringBuffer whereSql) {
		super();
		this.selectSql = selectSql;
		this.whereSql = whereSql;
		isFirst = false;
	}

	public void setOrderbySql(String orderbySql) {
		this.orderbySql = orderbySql;
	}

	public void setLimitSql(String limitSql) {
		this.limitSql = limitSql;
	}
	
	public StringBuffer getWhereSql() {
		return whereSql;
	}

	public void addAndParam(Symbol symbol,String andParam){
		if(!isFirst){
			whereSql.append(symbol.getSymbol());
		}
		isFirst = false;
		whereSql.append(andParam);
	}
	public String getCompleteSql(){
		StringBuffer completeSql = new StringBuffer();
		completeSql.append(selectSql);
		if(whereSql != null){
			completeSql.append(whereSql);
		}
		if(orderbySql != null){
			completeSql.append(orderbySql);
		}
		if(limitSql != null){
			completeSql.append(limitSql);
		}
		return completeSql.toString();
	}
}
