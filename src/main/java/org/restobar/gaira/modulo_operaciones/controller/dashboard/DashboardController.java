package org.restobar.gaira.modulo_operaciones.controller.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.CategorySales;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.EmployeeRanking;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.KpiDTO;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.MonthComparison;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.ProductRanking;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.SalesPoint;
import org.restobar.gaira.modulo_operaciones.service.dashboard.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idSucursal) {
        return ResponseEntity.ok(dashboardService.getDashboard(fechaInicio, fechaFin, idSucursal));
    }

    @GetMapping("/kpi")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<KpiDTO> getKpi(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idSucursal) {
        return ResponseEntity.ok(dashboardService.getKpi(fechaInicio, fechaFin, idSucursal));
    }

    @GetMapping("/sales-evolution")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<List<SalesPoint>> getSalesEvolution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idSucursal) {
        return ResponseEntity.ok(dashboardService.getSalesEvolution(fechaInicio, fechaFin, idSucursal));
    }

    @GetMapping("/sales-by-category")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<List<CategorySales>> getSalesByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idSucursal) {
        return ResponseEntity.ok(dashboardService.getSalesByCategory(fechaInicio, fechaFin, idSucursal));
    }

    @GetMapping("/month-comparison")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<MonthComparison> getMonthComparison(
            @RequestParam(required = false) Long idSucursal) {
        return ResponseEntity.ok(dashboardService.getMonthComparison(idSucursal));
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<List<ProductRanking>> getTopProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idSucursal,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getTopProducts(fechaInicio, fechaFin, idSucursal, limit));
    }

    @GetMapping("/employee-ranking")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<List<EmployeeRanking>> getEmployeeRanking(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idSucursal) {
        return ResponseEntity.ok(dashboardService.getEmployeeRanking(fechaInicio, fechaFin, idSucursal));
    }
}
