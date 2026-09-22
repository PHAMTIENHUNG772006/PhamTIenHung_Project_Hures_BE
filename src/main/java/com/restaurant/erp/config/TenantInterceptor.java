package com.restaurant.erp.config;

import com.restaurant.erp.common.context.BranchContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // Exclude authorization, websockets, and swagger documentation from branch checks
        if (requestURI.startsWith("/api/auth") || 
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
        BranchContext.clear();
    }
}
