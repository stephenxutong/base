package com.fpi.flyway;

import java.io.File;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import com.google.common.collect.Maps;

/**
 * @author xuxiangli
 */
@ConfigurationProperties(prefix = "spring")
public class FlywayBootDb {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlywayBootDb.class);

	private static final String DEFAULT_LOCATION = "db/";
	
	private static final String FILECOMMA = "file:";

	private static final String PREFIX = "spring.datasource.";

	private static final String URL = ".url";

	private static final String USER = ".username";

	private static final String PASSWORD = ".password";

	@Value("${flyway.location:" + DEFAULT_LOCATION + "}")
	private String location;

	@Autowired
	private Environment env;

	@PostConstruct
	public void flywayBoot() throws Exception {
		LOGGER.info("start to flyway");
		Map<String, DbSource> dbSourceMap = getDbSourceMap();
		for (String folder : dbSourceMap.keySet()) {
			String migrationFilesLocation = location + folder;
			LOGGER.info("location:" + migrationFilesLocation);
			DbSource dbSource = dbSourceMap.get(folder);
			Flyway flyway = new Flyway();
			flyway.setDataSource(dbSource.getUrl(), dbSource.getUser(), dbSource.getPassword());
			flyway.setLocations(migrationFilesLocation);
			flyway.setBaselineOnMigrate(true);
			flyway.repair();
			flyway.migrate();
		}
	}

	/**
	 * 封装所有的数据库 脚本统一的顶级目录为resources/db 
	 * key: 存放某一数据库脚本的目录 value: 某一数据库的连接信息 DbSource
	 * @return Map<String, DbSource> map
	 */
	public Map<String, DbSource> getDbSourceMap() {
		Map<String, DbSource> result = Maps.newHashMap();
		java.net.URL dbdir = Thread.currentThread().getContextClassLoader().getResource(location);
		LOGGER.info("base location: {}", dbdir);
		String realPath = dbdir.getPath();
		int pos = realPath.indexOf(FILECOMMA);
		if(pos >= 0) {
			realPath = realPath.substring(pos + FILECOMMA.length());
		}
		String[] folders = new File(realPath).list();
		if (null != folders) {
			for (String key : folders) {
				String url = env.getProperty(PREFIX + key + URL);
				String user = env.getProperty(PREFIX + key + USER);
				String pwd = env.getProperty(PREFIX + key + PASSWORD);
				if (null != url) {
					DbSource dbSource = new DbSource(url, user, pwd);
					result.put(key, dbSource);
				}
			}
		}
		return result;
	}
}