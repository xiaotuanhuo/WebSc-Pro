package sc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@MapperScan("sc.system.mapper")
@ComponentScan(basePackages = {
		"sc" }, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "sc.common.config.exculd.*"))
public class ShiroActionApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ShiroActionApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ShiroActionApplication.class, args);
	}
}