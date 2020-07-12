package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.common.constants.RoleEnum;
import sc.system.mapper.BureauMapper;
import sc.system.model.WebScBureau;
import sc.system.model.WebScUser;
import sc.system.model.vo.District;

@Service
public class BureauService {
	private static final Logger logger = LoggerFactory.getLogger(BureauService.class);
	
	@Resource
	private BureauMapper bureauMapper;
	
	@Autowired
	private District district;
	
    public List<WebScBureau> getTree() {
    	Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<WebScBureau> bureaus = bureauMapper.selectTree(user.getProvince(), user.getCity(), user.getArea());
		// 前端对表的操作由于插件的缘故，从后台生成操作代码
		// 其中编辑和删除对系统管理员、超级管理员、区域管理员可见
		boolean show = false;
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case XTGLY:
			case CJGLY:
			case QYGLY:
				show = true;
				break;
			default:
				break;
		}
		bureaus = getDistName(bureaus, show);
		return getDistName(bureaus, show);
    }
    
    public WebScBureau getBereauInfo(String bureauId) {
    	WebScBureau bureau = bureauMapper.selectByPrimaryKey(bureauId);
    	if (bureau.getProvince() != null) {
			bureau.setProvinceName(district.getDistrictMap().get(bureau.getProvince()).getName());
		}
    	if (bureau.getCity() != null) {
			bureau.setCityName(district.getDistrictMap().get(bureau.getCity()).getName());
		}
    	if (bureau.getArea() != null) {
			bureau.setAreaName(district.getDistrictMap().get(bureau.getArea()).getName());
		}
    	return bureau;
    }
    
    /**
	 * 递归获取集团所属行政区划的区划名称
	 * @param depts
	 * @return
	 */
	private List<WebScBureau> getDistName(List<WebScBureau> bureaus, boolean show) {
		if (bureaus.size() > 0) {
			for (WebScBureau bureau : bureaus) {
				String htmlDetail = "<a lay-event=\"detail" + bureau.getBureauId() + "\">\r\n" + 
						"		<i class=\"layui-icon layui-icon-form zadmin-oper-area zadmin-blue\" title=\"详情\"></i>\r\n" + 
						"	</a>";
				String htmlEdit = "<a lay-event=\"edit" + bureau.getBureauId() + "\">\r\n" + 
						"		<i class=\"zadmin-icon zadmin-icon-edit-square zadmin-oper-area zadmin-blue\" title=\"编辑\"></i>\r\n" + 
						"	</a>";
				String htmlDel = "<a lay-event=\"del" + bureau.getBureauId() + "\">\r\n" + 
						"		<i class=\"zadmin-icon zadmin-icon-delete zadmin-oper-area zadmin-red\" title=\"删除\"></i>\r\n" + 
						"	</a>";
				if (show) {
					bureau.setOperator(htmlDetail + htmlEdit + htmlDel);
				} else {
					bureau.setOperator(htmlDetail);
				}
				if (bureau.getProvince() != null) {
					bureau.setProvinceName(district.getDistrictMap().get(bureau.getProvince()).getName());
				}
				if (bureau.getCity() != null) {
					bureau.setCityName(district.getDistrictMap().get(bureau.getCity()).getName());
				}
				if (bureau.getArea() != null) {
					bureau.setAreaName(district.getDistrictMap().get(bureau.getArea()).getName());
				}
				if (show) {
					bureau.setOperator(htmlDetail + htmlEdit + htmlDel);
				} else {
					bureau.setOperator(htmlDetail);
				}
				if (bureau.getChildren().size() > 0) {
					getDistName(bureau.getChildren(), show);
				}
			}
		}
		return bureaus;
	}
}
