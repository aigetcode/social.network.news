package ru.news.endpoint;


import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerWrapper extends PostgreSQLContainer<PostgresContainerWrapper> {
    private static final String POSTGRES_IMAGE_NAME = "postgres:11";
    private static final String POSTGRES_DB = "integtest_module_tests_db";
    private static final String POSTGRES_USER = "test";
    private static final String POSTGRES_PASSWORD = "test";

    public PostgresContainerWrapper() {
        super(POSTGRES_IMAGE_NAME);
        this
                // if you need container logger
                // .withLogConsumer(new Slf4jLogConsumer(log))
                .withDatabaseName(POSTGRES_DB)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD)
                .withInitScript("postgres/init-db.sql");
    }

    @Override
    public void start() {
        super.start();
        String containerId = this.getContainerId();
        System.out.println("Postgres container id: " + containerId);
    }
}
