package com.ms.cloud.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Enrutador dinámico que selecciona entre la base de datos MASTER y
 * READ_REPLICA
 * basándose en si la transacción actual es de solo lectura.
 */
public class TransactionRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(TransactionRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType type = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? DataSourceType.READ_REPLICA
                : DataSourceType.MASTER;

        log.info("Routing database access to: [{}]", type);
        return type;
    }
}
