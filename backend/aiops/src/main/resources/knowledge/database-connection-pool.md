# 数据库连接池耗尽

当 HikariPool 报告 Connection is not available 或 connection timeout 时，检查活跃连接数、慢 SQL、连接泄漏和数据库最大连接数。连接池接近 maximumPoolSize 且接口延迟升高，通常说明请求在等待数据库连接。
