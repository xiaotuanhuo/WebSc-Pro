package sc.system.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

import ch.qos.logback.classic.pattern.Util;
import sc.common.util.Numbers;
import sc.system.mapper.DocMapper;
import sc.system.mapper.UserMapper;
import sc.system.mapper.WebScEvaluateMapper;
import sc.system.mapper.WebScLabelMapper;
import sc.system.model.WebScUser;
import sc.system.model.vo.Vote;

@Service
public class StatsService {
	private static final Logger log = LoggerFactory.getLogger(StatsService.class);
	
	@Resource
	private UserMapper userMapper;
	@Resource
	private DocMapper docMapper;
	@Resource
	private WebScEvaluateMapper evuMapper;
	@Resource
	private WebScLabelMapper labelMapper;
	
	public List<WebScUser> stats(int page, int rows, String name) {
		PageHelper.startPage(page, rows);
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<WebScUser> doctors = userMapper.selectDoctors(user, name);
		for (WebScUser doctor : doctors) {
			int todayCount = docMapper.statsToday(doctor.getUserId());
			int monthCount = docMapper.statsMonth(doctor.getUserId());
			int yearCount = docMapper.statsYear(doctor.getUserId());
			int sumCount = docMapper.statsSum(doctor.getUserId());
			
			doctor.setD_dr_qty(todayCount);
			doctor.setM_dr_qty(monthCount);
			doctor.setY_dr_qty(yearCount);
			doctor.setS_dr_qty(sumCount);
			if (doctor.getAvgEvaluate() == 0) {
				doctor.setScore("-");
			} else {
				doctor.setScore(doctor.getAvgEvaluate() + "");
			}
		}
		return doctors;
	}
	
	public List<Vote> vote(String userId) {
		List<Vote> votes = labelMapper.selectVote();	// 评价维度初始化
		String labelStr = evuMapper.selectLabels(userId);	// 医生被评价标签字符串拼接
		if (labelStr != null) {
			String[] labels = labelStr.split(",");
			Map<String, Integer> map = new HashMap<String, Integer>();		// 各个标签数目
			Map<String, Integer> pMap = new HashMap<String, Integer>();	// 相同父级标签的子标签数目合计
			// 统计各个标签数量
			for (int i = 0; i < labels.length; i++) {
				Integer opt = map.get(labels[i]);
				map.put(labels[i], opt == null ? 1 : opt + 1);
			}
			Set<Entry<String, Integer>> set = map.entrySet();
			for (Entry<String, Integer> entry : set) {
				Optional<Vote> optional = votes.stream().filter(item -> item.getLabel().equals(entry.getKey()))
						.findFirst();
				Vote vote = (Vote) optional.get();
				vote.setValue(entry.getValue());	// 标签数量
				Integer pOpt = pMap.get(vote.getType());
				pMap.put(vote.getType(), pOpt == null ? entry.getValue() : (pOpt + entry.getValue()));		// 统计子标签的合计 计算比例用
			}
			// 比例计算
			Set<Entry<String, Integer>> pSet = pMap.entrySet();
			for (Entry<String, Integer> entry : pSet) {
				Stream<Vote> streams = votes.stream().filter(item -> item.getType().equals(entry.getKey()));
				// 迭代计算比例
				Iterator<Vote> it = streams.iterator();
				while (it.hasNext()) {
					Vote vote = it.next();
					vote.setRatio(Numbers.div(vote.getValue(), entry.getValue(), 3));
				}
			}
		}
		return votes;
	}

}
