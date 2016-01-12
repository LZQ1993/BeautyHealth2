package com.LocationEntity;

import android.content.Context;

import com.infrastructure.CWDomain.EntityBase;
import com.infrastructure.CWSqliteManager.ISqlHelper;
import com.infrastructure.CWSqliteManager.SqliteHelper;

import java.util.List;

public class ServiceState extends EntityBase {
   public String ServiceName;
   public String CurrentState;
   private String ServiceFullName="com.LocationEntity.ServiceState";
   
   public void setCurrentServiceStateInDB(String Starting,Context context) {
	ISqlHelper mysqlhelper=new SqliteHelper(null,context);
	mysqlhelper.CreateTable(ServiceFullName);
	List<Object> objs=mysqlhelper.Query(ServiceFullName, "ServiceName='"+this.ServiceName+"'");

	ServiceState servicestate;
	if(objs!=null&&objs.size()>0){
		servicestate=(ServiceState)objs.get(0);
		servicestate.CurrentState=Starting;
		mysqlhelper.Update(servicestate);
	}else{
		servicestate=new ServiceState();
		servicestate.ServiceName=this.ServiceName;	
		servicestate.CurrentState=Starting;
		mysqlhelper.Insert(servicestate);
	}
   }
   public boolean requireStart(Context context){
	   ISqlHelper mysqlhelper=new SqliteHelper(null, context);
		List<Object> objs=mysqlhelper.Query(ServiceFullName, "ServiceName='"+this.ServiceName+"'");
		if(objs!=null&&objs.size()>0){
			ServiceState aservier=(ServiceState)objs.get(0);
			if(aservier.CurrentState.equals("1")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
   }
   
}
