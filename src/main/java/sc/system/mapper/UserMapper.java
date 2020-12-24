package sc.system.mapper;

import sc.system.model.WebScUser;
import sc.system.model.vo.UserVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {
	
	/**
	 * 根据微信openid获取用户信息
	 * @param openid
	 * @return
	 */
	@Select("SELECT * FROM WSC_USER WHERE wx_openid=#{openid}")
	WebScUser selectUserByOpenid(@Param("openid") String openid);
	
	/**
	 * 修改微信openid
	 * @param openid
	 * @param userId
	 * @return
	 */
	@Update("UPDATE WSC_USER SET wx_openid=#{openid} WHERE user_id=${userId}")
	int updOpenid(@Param("openid") String openid, @Param("userId") int userId);
	
	/**
	 * 判断指定姓名的医生是否存在
	 * @param doctorName 医生姓名
	 * @return 大于0表示存在
	 */
	@Select("SELECT COUNT(*) FROM WSC_USER WHERE role_id='5' AND user_name=#{doctorName}")
	int isExistByDoctorName(@Param("doctorName") String doctorName);
	
	/**
	 * 获取指定区域的医生
	 * @param cityPre 区域编码，包括province和city
	 * @return
	 */
	@Select("SELECT * FROM WSC_USER WHERE role_id='5' AND city LIKE CONCAT(#{cityPre},'%')")
	List<WebScUser> selectDoctorsByQy(@Param("cityPre") String cityPre);
	
    int deleteByPrimaryKey(Integer userid);

    int insert(WebScUser user);

    WebScUser selectByPrimaryKey(Integer userid);

    int updateByPrimaryKey(WebScUser user);

    int updateByUser(WebScUser user);
    
    /**
     * 获取用户所拥有的所有权限
     */
    Set<String> selectPermsByLoginName(@Param("loginname") String loginname);

    /**
     * 获取用户所拥有的所有角色
     */
    Set<String> selectRoleNameByLoginName(@Param("loginname") String loginname);

    /**
     * 根据用户名获取用户
     */
    WebScUser selectOneByLoginName(@Param("loginname") String loginname);

    /**
     * 获取所有用户
     */
    List<WebScUser> selectAll();

    /**
     * 获取所有用户
     */
    List<WebScUser> selectAllWithGroup(WebScUser userQuery);

    /**
     * 根据用户角色区划查询列表
     * @param user
     * @return
     */
    List<WebScUser> selectWithRoleAndDist(@Param("user") UserVO user);
    
    List<WebScUser> selectByRoleTypeId(@Param("roleTypeId") String roleTypeId);
    
    /**
     * 更改用户的状态为某项值
     */
    int updateStatusByPrimaryKey(@Param("id") Integer id, @Param("status") String status);

    /**
     * 更新用户最后登录事件
     */
    int updateLastLoginTimeByLoginName(@Param("loginname") String loginname);

    /**
     * 统计已经有几个此用户名, 用来检测用户名是否重复.
     */
    int countByLoginName(@Param("loginname") String loginname);

    /**
     * 统计已经有几个此用户名, 用来检测用户名是否重复 (不包含某用户 ID).
     */
    int countByLoginNameNotIncludeUserId(@Param("loginname") String loginname, @Param("userId") Integer userid);

    /**
     * 查询此用户拥有的所有角色的 ID
     *
     * @param userid 用户 ID
     * @return 拥有的角色 ID 数组
     */
    Integer[] selectRoleIdsByUserId(@Param("userid") Integer userid);

//    /**
//     * 根据邮箱激活码, 查询要被激活的用户.
//     */
//    User selectByActiveCode(@Param("activeCode") String activeCode);

    /**
     * 统计系统中有多少个用户.
     */
    int count();

    /**
     * 获取用户所拥有的操作权限
     */
    Set<String> selectOperatorPermsByLoginName(@Param("loginname") String loginname);

    int updatePasswordByUserId(@Param("userId") Integer userId, @Param("password") String password, @Param("salt") String salt);

    int activeUserByUserId(Integer userid);

//    selectAllByUsernameLikeAndStatus

}