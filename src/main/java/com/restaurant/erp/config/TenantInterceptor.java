package com.restaurant.erp.config;

import com.restaurant.erp.common.context.BranchContext;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    private final EntityManager entityManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // Exclude authorization, websockets, and swagger documentation from branch checks
        if (requestURI.startsWith("/api/auth") || 
            requestURI.startsWith("/api/users/login") || 
            requestURI.startsWith("/api/users/register") || 
            requestURI.startsWith("/api/branches") || 
            requestURI.startsWith("/swagger-ui") || 
            requestURI.startsWith("/v3/api-docs") || 
            requestURI.startsWith("/ws/kds")) {
            return true;
        }

        String branchIdHeader = request.getHeader("X-Branch-Id");
        if (branchIdHeader == null || branchIdHeader.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"X-Branch-Id header is required\",\"data\":null}");
            return false;
        }

        try {
            Integer branchId = Integer.parseInt(branchIdHeader);
            BranchContext.setCurrentBranchId(branchId);

            // Enable Hibernate tenantFilter on current session if available
            try {
                Session session = entityManager.unwrap(Session.class);
                if (session != null) {
                    session.enableFilter("tenantFilter").setParameter("branchId", branchId.longValue());
                }
            } catch (Exception ex) {
                log.debug("Session filter will be attached by aspect: {}", ex.getMessage());
            }

            return true;
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"X-Branch-Id must be a valid integer\",\"data\":null}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            Session session = entityManager.unwrap(Session.class);
            if (session != null) {
                session.disableFilter("tenantFilter");
            }
        } catch (Exception ignored) {
        } finally {
            BranchContext.clear();
        }
    }
}

