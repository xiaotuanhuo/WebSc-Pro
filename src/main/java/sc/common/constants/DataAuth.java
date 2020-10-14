package sc.common.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装由角色、机构、行政区划不同产生数据权限问题的请求参数
 * @author aisino
 *
 */
public class DataAuth {

	/**
	 * 根据当前用户角色返回可操作角色列表
	 * @param roleId
	 * @return
	 */
	public static List<Integer> getRoleList(Integer roleId) {
		List<Integer> list = new ArrayList<Integer>();
		RoleEnum re = RoleEnum.valueOf(roleId);
		switch (re) {
			case XTGLY:
				// 系统管理员仅可操作超级管理员角色
				list.add(RoleEnum.CJGLY.getCode());
				break;
			case CJGLY:
			case QYGLY:
				// 超级管理员，区域管理员可操作角色：区域管理员、区域订单录入员、卫监局管理员、医疗机构管理员、医生、护士、机构订单录入员
				list.add(RoleEnum.QYGLY.getCode());
				list.add(RoleEnum.QYDDLRY.getCode());
				list.add(RoleEnum.WJJGLY.getCode());
				list.add(RoleEnum.YLJGGLY.getCode());
				list.add(RoleEnum.YS.getCode());
				list.add(RoleEnum.HS.getCode());
				list.add(RoleEnum.JGDDLRY.getCode());
				break;
			case YLJGGLY:
				// 医疗机构管理员可操作角色：机构订单录入员
				list.add(RoleEnum.JGDDLRY.getCode());
				break;
			default:
				break;
		}
		return list;
	}
}
