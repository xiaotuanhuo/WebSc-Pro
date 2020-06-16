package sc.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import sc.system.model.UserToken;

@Mapper
public interface UserTokenMapper {
	int updateUserToken(UserToken ut);
	
	int insertUserToken(UserToken ut);
	
	UserToken selectUserTokenByUserId(int userId);
	
	UserToken selectUserTokenByToken(String token);
}
