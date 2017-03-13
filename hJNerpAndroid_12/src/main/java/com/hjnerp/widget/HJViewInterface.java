package com.hjnerp.widget;

import com.hjnerp.model.BusinessData;

public interface HJViewInterface {
//	public void setValue(ArrayList<Ctlm1347> ctlm1347List);
void setValue(String msg);
	void setValueDefault();
	void setValue(String row, String column, String value);
	String getValue(String row, String column);
	String getValue();
	void setJesonValue(String values);
 
	String setLocation();   ///设置定位
	String setPhoto() ; //拍一次照片
	
	String getDataSource();
	String getID();
	boolean getEditable();
	
	String getRowCount();
	String getCurrentRow();
	 
	void  addItem(String billno, String nodeid, String vlues);
	
	void setDataSource(String Data);
	void setDataBuild(Boolean flag, BusinessData ctlm1345List);
	
	int saveData(Boolean required);
	
	
	boolean validate();

}
