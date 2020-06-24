package sc;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.javassist.expr.NewArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import sc.system.mapper.OrganizationMapper;
import sc.system.mapper.RecordMapper;
import sc.system.mapper.WebScCalendarMapper;
import sc.system.model.Record;
import sc.system.model.WebScCalendarAid;
import sc.system.model.WebScOrganization;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("sc.system.mapper")
public class ShiroActionApplicationTest{
	@Autowired
	RecordMapper recordMapper;
	@Autowired
	OrganizationMapper organizationMapper;
	@Autowired
	WebScCalendarMapper webScCalendarMapper;
	
	@Test
	public void test() {
		try {
			Map<String, Object> paraMap = new HashMap<String, Object>();
//			paraMap.put("doctorName", "一一");
//			paraMap.put("orgName", "医疗机构0");
			paraMap.put("startDate", "2020-06-21 09:23:33");
			paraMap.put("endDate", "2020-06-22 09:23:33");
			List<WebScCalendarAid> webScCalendarAids = 
					webScCalendarMapper.selectWebScCalendarAidsByConditions(paraMap);
			
			System.out.println("webScCalendarAids : "+webScCalendarAids);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		WebScOrganization organization = organizationMapper.selectWebScOrganization("医疗机构0");
//		System.out.println("organization : "+organization);
		
//		List<Record> records = recordMapper.selectRecords();
//		System.out.println("records : "+records);
	}
	
}