package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.hjnerp.business.BusinessLua;
import com.hjnerp.dao.QiXinBaseDao;
import com.hjnerp.db.Tables;
import com.hjnerp.model.ChatHisBean;
import com.hjnerp.model.GroupInfo;
import com.hjnerp.model.VerfifyFriendInfo;
import com.hjnerp.net.HttpUtils;
import com.hjnerp.util.LuaLoadScript;

public class MyAndroidTestCase extends AndroidTestCase
{
	public static final String TAG = "MyAndroidTestCase";

	// 测试表创建语句
	public void testTables()
	{
		Log.w(TAG, Tables.UserTable.getCreateSQLString());
		Log.w(TAG, Tables.ContactTable.getCreateSQLString());
		Log.w(TAG, Tables.EnterpriseInfoTable.getCreateSQLString());
		Log.w(TAG, Tables.GroupInfoTable.getCreateSQLString());
		Log.w(TAG, Tables.GroupRelationTable.getCreateSQLString());
	}

	// 测试是否能查到某一组id的所有成员
	public void testQueryGroupRelation()
	{
		String[] users = QiXinBaseDao.queryGroupRelations("b373a87d-737b-4f9b-b8d0-bdcbb7d608a0");
		Log.w(TAG, Arrays.toString(users));
	}

	// 测试是否能查到所有组信息
	public void testQueryGroupInfo()
	{
		List<GroupInfo> users = QiXinBaseDao.queryAllGroupInfo();
		Log.w(TAG, users.toString());
	}
	
	//测试是否查到正确的最后一条聊天信息
	public void testQueryLastChatHisBeans()
	{
		ArrayList<ChatHisBean> al = QiXinBaseDao.queryLastChatHisBeans();
		Log.w(TAG, al.toString());
	}
	
	//测试插入临时表
	public void testInsertTempContactInfo()
	{
		VerfifyFriendInfo tci = new VerfifyFriendInfo();
		tci.setComid("com-333");
		tci.setDeptid("dep1");
		tci.setDeptname("shafa");
		tci.setFriendid("fff");
		tci.setFriendmtel("33499324");
		tci.setFriendname("fff");
		tci.setFriendOS("ADf");
		QiXinBaseDao.replaceTempContactInfo(tci);
	}
	
	//测试删除临时表记录
	public void testDeleteTempContactInfo()
	{
		QiXinBaseDao.deleteTempContactInfoById("");
	}
	
	//测试转移临时表记录到联系人表中
	public void testShiftTempContactInfo()
	{
		QiXinBaseDao.shiftTempContactInfoById("fff");
	}
	
	//测试查询临时表所有记录
	public void testQueryTempContactInfo()
	{
		ArrayList<VerfifyFriendInfo> tcis = QiXinBaseDao.queryTempContactInfos(null);
		System.out.println(tcis);
	}
	
	//测试删除群信息
	public void testRemoveGroupInfo()
	{
		QiXinBaseDao.deleteGroupInfoById("814dd642-4d34-4561-97b3-132309a33c61");
	}
	
	//测试删除群关系
	public void testRemoveGroupRelations()
	{
		QiXinBaseDao.deleteGroupRelations("497c7a7a-9d59-4e53-95bd-095fbea08027", "chenzhihui","zhaoyi","lixiang");
	}
	
	//测试删除群聊天记录
	public void testRemoveGroupMsg()
	{
		QiXinBaseDao.deleteGroupMsgById("57db2fe4-df47-40d2-b43c-9661e906b015");
	}
	
	//测试工作流列表返回数据
	public void testWorkflowList()
	{
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("com_id", "CM1008-0001");
		parameters.put("session_id", "aaxx");
		parameters.put("type", "list");
		HttpUtils.post("http://192.168.0.165:8080/nerp/servlet/workflowMobileServlet", parameters, HttpUtils.ENCODE_UTF8);
	}
	
	
	public void testBMapDistance()
	{
		BusinessLua lua = new BusinessLua();
		String result = lua.getbmap_distance("29.490295", "106.486654", "29.615467", "106.581515");
		assertEquals(result.split("\\.")[0], "16670");
	}
	
	@SmallTest 
	public void testLuaBMapDistance() throws Exception
	{
//		LuaLoadScript.LoadScript(null, getContext());
		String result = LuaLoadScript.runScriptreturn("hjbmap_getdistance('29.490295','106.486654','29.615467','106.581515')");
		assertEquals(result.split("\\.")[0], "16670");
	}
}
