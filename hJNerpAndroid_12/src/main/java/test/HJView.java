package test;
//<textboxList id = "003001" name = "终端信息"></textboxList>
//<textarea id = "003002" name = "拜访回顾"></textarea>
//<location id = "003003" name = "定位"></location>
//<textbox id = "003004" name = "进店时间"></textbox>
//<readiobutton id = "003005" name = "终端状态"></readiobutton>
//<checkbox id = "003006" name = "是否有主管协访"></checkbox>
//<cusbutton id = "003007" name = "终端信息修改"></cusbutton>
//<photo id = "003008" name = "拍照"></photo>
public class HJView {
	private String id;
	private String name;
	
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return id;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
