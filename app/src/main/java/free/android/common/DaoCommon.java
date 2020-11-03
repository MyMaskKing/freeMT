package free.android.common;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DaoCommon {

	public static void main(String[] args) {
		try {
			@SuppressWarnings("unused")
			ApplicationContext applicationContext =new ClassPathXmlApplicationContext("spring-db.xml");
			DaoCommon bean = (DaoCommon)applicationContext.getBean("daoAspect");
			System.out.println(bean);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Autowired
	public static SqlSession sqlSession;

	/**
	 * 加载Log打印(org.slf4j.Logger)
	 */
	private Logger log = LoggerFactory.getLogger(DaoCommon.class);

	private SqlSessionFactory sqlSessionFactory;

	/**
	 * @return the sqlSessionFactory
	 */
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	/**
	 * @param sqlSessionFactory
	 *            the sqlSessionFactory to set
	 */
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	/**
	 * 开启sqlSession
	 */
	private void before() {
		log.info("开启SqlSession. ing......");
		sqlSession = sqlSessionFactory.openSession();
		log.info("SqlSession:[" + sqlSession + "]");
	}

	/**
	 * 关闭sqlSession
	 */
	private void after() {
		log.info("Sql执行Commit. ing......");
		sqlSession.commit();
		log.info("关闭SqlSession. ing......");
		sqlSession.close();
	}
}
