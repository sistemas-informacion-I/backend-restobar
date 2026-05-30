package org.restobar.gaira.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VersionFixInitializer {

    private static final Logger log = LoggerFactory.getLogger(VersionFixInitializer.class);

    @PersistenceContext
    private EntityManager em;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void fixNullVersions() {
        int stockUpdated = em.createNativeQuery(
                "UPDATE stock_sucursal SET version = 0 WHERE version IS NULL")
                .executeUpdate();

        int lotesUpdated = em.createNativeQuery(
                "UPDATE lote_inventario SET version = 0 WHERE version IS NULL")
                .executeUpdate();

        if (stockUpdated > 0 || lotesUpdated > 0) {
            log.info("Corregidas versiones nulas: {} stock_sucursal, {} lote_inventario",
                    stockUpdated, lotesUpdated);
        }
    }
}
