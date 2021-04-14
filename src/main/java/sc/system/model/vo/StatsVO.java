package sc.system.model.vo;

/**
 * 统计报表属性类
 * @author aisino
 *
 */
public class StatsVO {
	// 统计条件
	
	
	// 医生报表统计属性
	private String name;	// 医生姓名
	private int score;		// 评分;
	private int drDQty;		// 日手术量
	private int drMQty;		// 月手术量
	private int drYQty;		// 年手术量
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getDrDQty() {
		return drDQty;
	}
	
	public void setDrDQty(int drDQty) {
		this.drDQty = drDQty;
	}
	
	public int getDrMQty() {
		return drMQty;
	}
	
	public void setDrMQty(int drMQty) {
		this.drMQty = drMQty;
	}
	
	public int getDrYQty() {
		return drYQty;
	}
	
	public void setDrYQty(int drYQty) {
		this.drYQty = drYQty;
	}
}
