package sc.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import sc.system.model.UserData;
import sc.system.model.UserOpenId;
@Mapper
public interface UserOpenIdMapper {
	int updateUserOpenId(UserOpenId uo);
	
	int insertUserOpenId(UserOpenId uo);
	
	UserOpenId selectUserOpenIdByUserId(int userId);
	
	UserOpenId selectUserOpenIdByOpenId(String openid);
	
	UserData selectUserDataByOpendId(String openId);
}
