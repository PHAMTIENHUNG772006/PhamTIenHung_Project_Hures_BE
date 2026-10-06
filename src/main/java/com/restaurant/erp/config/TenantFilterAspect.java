package com.restaurant.erp.config;

import com.restaurant.erp.common.context.BranchContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Repository *)")
    public void applyTenantFilter() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId != null) {
            try {
                Session session = entityManager.unwrap(Session.class);
                if (session != null && session.isOpen()) {
                    session.enableFilter("tenantFilter").setParameter("branchId", branchId.longValue());
                }
            } catch (Exception ex) {
                log.trace("TenantFilterAspect: Could not enable filter: {}", ex.getMessage());
            }
        }
    }
}
