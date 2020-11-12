package com.usoft.mybatis.utils.cat;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.stereotype.Component;

import java.util.Properties;


/**
 * 对MyBatis进行拦截，添加Cat监控
 *
 * @author wangcanyi
 */

@Intercepts({
		@Signature(method = "query", type = Executor.class, args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
		@Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})
})
@Component
public class CatMybatisInterceptor implements Interceptor {
	private static final String EMPTY_CONNECTION = "jdbc:mysql://unknown:3306/%s?useUnicode=true";
	private Properties properties;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		String methodName = this.getMethodName(mappedStatement);
		Transaction t = Cat.newTransaction("SQL", methodName);
		String sqlURL = this.getSqlURL(mappedStatement);
		Cat.logEvent("SQL.Database", sqlURL);
		//注意：不进行记录SQL语句与参数，原因：当SQL语句过长时，存在性能问题，例如：批量insert
		try {
			Object returnObj = invocation.proceed();
			t.setStatus(Transaction.SUCCESS);
			return returnObj;
		} catch (Exception e) {
			Cat.logError(e);
			t.setStatus(e);
			throw e;
		} finally {
			t.complete();
		}
	}

	/**
	 * 获取方法名
	 *
	 * @param mappedStatement
	 * @return
	 */
	private String getMethodName(MappedStatement mappedStatement) {
		String[] strArr = mappedStatement.getId().split("\\.");
		String methodName = strArr[strArr.length - 2] + "." + strArr[strArr.length - 1];
		return methodName;
	}

	/**
	 * 获取数据库连接地址
	 *
	 * @return
	 */
	private String getSqlURL(MappedStatement mappedStatement) {
		javax.sql.DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
		if (dataSource == null) {
			return EMPTY_CONNECTION;
		}
		if (dataSource instanceof DataSource) {
			return ((DataSource) dataSource).getUrl();
		}
		return EMPTY_CONNECTION;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
