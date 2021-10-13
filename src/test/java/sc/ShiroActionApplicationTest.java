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

import sc.system.mapper.DocMapper;
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
	@Autowired
	DocMapper docMapper;
	
	@Test
	public void test() {
//		List<String> documentIds = docMapper.selectByDocumentState("4", "2021-10-13 00:00:00");
//		System.out.println(documentIds);
		
		int c = docMapper.updDocumentState("5", "00DRTf56dgwXnWk4pZI");
		System.out.println("c="+c);
	}
	
}