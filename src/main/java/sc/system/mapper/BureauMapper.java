package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import sc.system.model.WebScBureau;

public interface BureauMapper {
	int deleteByPrimaryKey(String bureauId);
	
	int insert(WebScBureau record);
	
	int insertSelective(WebScBureau record);
	
	WebScBureau selectByPrimaryKey(String bureauId);
	
	WebScBureau selectSuperBureau();
	
	/**
     * 查询当前节点及其子节点
     * @param bureauId
     * @return
     */
	List<WebScBureau> selectTree(@Param("bureau_id") String bureau_id, @Param("province_code") String province,
			@Param("city_code") String city, @Param("area_code") String area);
	
	/**
	 * 查询当前非叶子区划及其下级非叶子节点
	 * @param privince
	 * @param city
	 * @param area
	 * @return
	 */
	List<WebScBureau> selectUnleafTree(@Param("province_code") String province,
			@Param("city_code") String city, @Param("area_code") String area);
	
	WebScBureau selectSuper(@Param("province_code") String province,
			@Param("city_code") String city, @Param("area_code") String area);
	
	int updateByPrimaryKeySelective(WebScBureau record);
	
	int updateByPrimaryKey(WebScBureau record);
	
	/**
	 * 统计当前区划下的卫监局数量
	 * @param province
	 * @param city
	 * @param area
	 * @return
	 */
	int countByDist(@Param("province_code") String province, @Param("city_code") String city,
			@Param("area_code") String area);
	
	/**
     * 统计该卫监局名称数量
     * @param bureauId	不为null是表示修改，统计数量时不包含该项
     * @param bureauName
     * @return
     */
	int countByName(@Param("bureau_id") String bureauId, @Param("bureau_name") String bureauName);
}