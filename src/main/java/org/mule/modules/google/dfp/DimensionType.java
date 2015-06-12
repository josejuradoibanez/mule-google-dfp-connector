package org.mule.modules.google.dfp;

public class DimensionType {

	private boolean _MONTH_AND_YEAR;
	private boolean _WEEK;
	private boolean _DATE;
	private boolean _DAY;
	private boolean _HOUR;
	private boolean _LINE_ITEM_ID;
	private boolean _LINE_ITEM_NAME;
	private boolean _LINE_ITEM_TYPE;
	private String mystring;
	
//	public DimensionType(){}
	
	public boolean is_MONTH_AND_YEAR() {
		return _MONTH_AND_YEAR;
	}
	public void set_MONTH_AND_YEAR(boolean _MONTH_AND_YEAR) {
		this._MONTH_AND_YEAR = _MONTH_AND_YEAR;
	}
	public boolean is_WEEK() {
		return _WEEK;
	}
	public void set_WEEK(boolean _WEEK) {
		this._WEEK = _WEEK;
	}
	public boolean is_DATE() {
		return _DATE;
	}
	public void set_DATE(boolean _DATE) {
		this._DATE = _DATE;
	}
	public boolean is_DAY() {
		return _DAY;
	}
	public void set_DAY(boolean _DAY) {
		this._DAY = _DAY;
	}
	public boolean is_HOUR() {
		return _HOUR;
	}
	public void set_HOUR(boolean _HOUR) {
		this._HOUR = _HOUR;
	}
	public boolean is_LINE_ITEM_ID() {
		return _LINE_ITEM_ID;
	}
	public void set_LINE_ITEM_ID(boolean _LINE_ITEM_ID) {
		this._LINE_ITEM_ID = _LINE_ITEM_ID;
	}
	public boolean is_LINE_ITEM_NAME() {
		return _LINE_ITEM_NAME;
	}
	public void set_LINE_ITEM_NAME(boolean _LINE_ITEM_NAME) {
		this._LINE_ITEM_NAME = _LINE_ITEM_NAME;
	}
	public boolean is_LINE_ITEM_TYPE() {
		return _LINE_ITEM_TYPE;
	}
	public void set_LINE_ITEM_TYPE(boolean _LINE_ITEM_TYPE) {
		this._LINE_ITEM_TYPE = _LINE_ITEM_TYPE;
	}
	public String getMystring() {
		return mystring;
	}
	public void setMystring(String mystring) {
		this.mystring = mystring;
	}


}
