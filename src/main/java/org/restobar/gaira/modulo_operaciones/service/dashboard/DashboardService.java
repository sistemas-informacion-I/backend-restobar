package org.restobar.gaira.modulo_operaciones.service.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.restobar.gaira.modulo_comercial.repository.detalleNotaVenta.DetalleNotaVentaRepository;
import org.restobar.gaira.modulo_comercial.repository.notaVenta.NotaVentaRepository;
import org.restobar.gaira.modulo_electronico.repository.ReservaRepository;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.CategorySales;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.EmployeeRanking;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.KpiDTO;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.MonthComparison;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.ProductRanking;
import org.restobar.gaira.modulo_operaciones.dto.dashboard.DashboardResponse.SalesPoint;
import org.restobar.gaira.modulo_operaciones.repository.ComandaRepository;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class DashboardService {

    private static final String CACHE_PREFIX = "dashboard:";
    private static final long CACHE_KPI_TTL_MINUTES = 5;
    private static final long CACHE_HISTORIC_TTL_MINUTES = 30;

    private final NotaVentaRepository notaVentaRepository;
    private final DetalleNotaVentaRepository detalleNotaVentaRepository;
    private final ComandaRepository comandaRepository;
    private final StockSucursalRepository stockSucursalRepository;
    private final ReservaRepository reservaRepository;
    private final SecurityUtils securityUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    public DashboardResponse getDashboard(LocalDate fechaInicio, LocalDate fechaFin, Long idSucursal) {
        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = LocalDate.now();

        Long sucursalId = resolveSucursalId(idSucursal);

        String cacheKey = buildCacheKey(sucursalId, "full", fechaInicio, fechaFin);
        DashboardResponse cached = (DashboardResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        KpiDTO kpis = getKpi(fechaInicio, fechaFin, sucursalId);
        List<SalesPoint> salesEvolution = getSalesEvolution(fechaInicio, fechaFin, sucursalId);
        List<CategorySales> salesByCategory = getSalesByCategory(fechaInicio, fechaFin, sucursalId);
        MonthComparison monthComparison = getMonthComparison(sucursalId);
        List<ProductRanking> topProducts = getTopProducts(fechaInicio, fechaFin, sucursalId, 10);
        List<EmployeeRanking> employeeRanking = getEmployeeRanking(fechaInicio, fechaFin, sucursalId);

        DashboardResponse response = DashboardResponse.builder()
                .kpis(kpis)
                .salesEvolution(salesEvolution)
                .salesByCategory(salesByCategory)
                .monthComparison(monthComparison)
                .topProducts(topProducts)
                .employeeRanking(employeeRanking)
                .build();

        redisTemplate.opsForValue().set(cacheKey, response, CACHE_KPI_TTL_MINUTES, TimeUnit.MINUTES);
        return response;
    }

    @Transactional(readOnly = true)
    public KpiDTO getKpi(LocalDate fechaInicio, LocalDate fechaFin, Long idSucursal) {
        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = LocalDate.now();

        Long sucursalId = resolveSucursalId(idSucursal);
        String cacheKey = buildCacheKey(sucursalId, "kpi", fechaInicio, fechaFin);

        KpiDTO cached = (KpiDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        Object[] totals = notaVentaRepository.findKpiTotalsWithAverage(sucursalId, inicio, fin);
        BigDecimal ventas = (BigDecimal) totals[0];
        int transacciones = ((Number) totals[1]).intValue();
        BigDecimal ticketPromedio = (BigDecimal) totals[2];

        Object[] profitData = detalleNotaVentaRepository.findProfit(sucursalId, inicio, fin);
        BigDecimal ganancia = (BigDecimal) profitData[0];
        BigDecimal ingresos = (BigDecimal) profitData[1];
        BigDecimal margen = ingresos.compareTo(BigDecimal.ZERO) > 0
                ? ganancia.multiply(BigDecimal.valueOf(100)).divide(ingresos, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long comandasActivas = sucursalId != null
                ? comandaRepository.countActive(sucursalId)
                : comandaRepository.countActive(null);

        long reservasDelDia = sucursalId != null
                ? reservaRepository.countBySucursalAndFecha(sucursalId, LocalDate.now())
                : reservaRepository.countBySucursalAndFecha(null, LocalDate.now());

        long stockCritico = sucursalId != null
                ? stockSucursalRepository.countStockCritico(sucursalId)
                : stockSucursalRepository.countStockCriticoGlobal();

        KpiDTO kpi = KpiDTO.builder()
                .ventasDelDia(ventas)
                .numeroTransacciones(transacciones)
                .ticketPromedio(ticketPromedio)
                .totalGanancia(ganancia)
                .margenGanancia(margen)
                .comandasActivas((int) comandasActivas)
                .reservasDelDia((int) reservasDelDia)
                .alertasStockCritico((int) stockCritico)
                .build();

        redisTemplate.opsForValue().set(cacheKey, kpi, CACHE_KPI_TTL_MINUTES, TimeUnit.MINUTES);
        return kpi;
    }

    @Transactional(readOnly = true)
    public List<SalesPoint> getSalesEvolution(LocalDate fechaInicio, LocalDate fechaFin, Long idSucursal) {
        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = LocalDate.now();

        Long sucursalId = resolveSucursalId(idSucursal);
        String cacheKey = buildCacheKey(sucursalId, "sales-evolution", fechaInicio, fechaFin);

        @SuppressWarnings("unchecked")
        List<SalesPoint> cached = (List<SalesPoint>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Object[]> rows = notaVentaRepository.findSalesEvolution(sucursalId, inicio, fin);
        List<SalesPoint> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(SalesPoint.builder()
                    .fecha(((java.sql.Date) row[0]).toLocalDate())
                    .total((BigDecimal) row[1])
                    .count(((Number) row[2]).intValue())
                    .build());
        }

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_HISTORIC_TTL_MINUTES, TimeUnit.MINUTES);
        return result;
    }

    @Transactional(readOnly = true)
    public List<CategorySales> getSalesByCategory(LocalDate fechaInicio, LocalDate fechaFin, Long idSucursal) {
        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = LocalDate.now();

        Long sucursalId = resolveSucursalId(idSucursal);
        String cacheKey = buildCacheKey(sucursalId, "sales-category", fechaInicio, fechaFin);

        @SuppressWarnings("unchecked")
        List<CategorySales> cached = (List<CategorySales>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Object[]> rows = detalleNotaVentaRepository.findSalesByCategory(sucursalId, inicio, fin);
        BigDecimal totalGeneral = BigDecimal.ZERO;
        List<CategorySales> result = new ArrayList<>();

        for (Object[] row : rows) {
            BigDecimal total = (BigDecimal) row[1];
            totalGeneral = totalGeneral.add(total);
            result.add(CategorySales.builder()
                    .categoria((String) row[0])
                    .total(total)
                    .porcentaje(0.0)
                    .build());
        }

        for (CategorySales cs : result) {
            double pct = totalGeneral.compareTo(BigDecimal.ZERO) > 0
                    ? cs.getTotal().divide(totalGeneral, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;
            cs.setPorcentaje(pct);
        }

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_HISTORIC_TTL_MINUTES, TimeUnit.MINUTES);
        return result;
    }

    @Transactional(readOnly = true)
    public MonthComparison getMonthComparison(Long idSucursal) {
        Long sucursalId = resolveSucursalId(idSucursal);
        String cacheKey = buildCacheKey(sucursalId, "month-comparison", LocalDate.now(), LocalDate.now());

        MonthComparison cached = (MonthComparison) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMesActual = hoy.withDayOfMonth(1);
        LocalDate inicioMesAnterior = inicioMesActual.minusMonths(1);
        LocalDate finMesAnterior = inicioMesActual.minusDays(1);

        BigDecimal mesActual = notaVentaRepository.sumTotalBySucursalAndFecha(
                sucursalId, inicioMesActual.atStartOfDay(), hoy.atTime(LocalTime.MAX));

        BigDecimal mesAnterior = notaVentaRepository.sumTotalBySucursalAndFecha(
                sucursalId, inicioMesAnterior.atStartOfDay(), finMesAnterior.atTime(LocalTime.MAX));

        BigDecimal variacion = mesAnterior.compareTo(BigDecimal.ZERO) > 0
                ? mesActual.subtract(mesAnterior).multiply(BigDecimal.valueOf(100))
                        .divide(mesAnterior, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        MonthComparison mc = MonthComparison.builder()
                .mesActual(mesActual)
                .mesAnterior(mesAnterior)
                .variacion(variacion)
                .periodoActual(inicioMesActual.format(DateTimeFormatter.ofPattern("MMM yyyy")))
                .periodoAnterior(inicioMesAnterior.format(DateTimeFormatter.ofPattern("MMM yyyy")))
                .build();

        redisTemplate.opsForValue().set(cacheKey, mc, CACHE_HISTORIC_TTL_MINUTES, TimeUnit.MINUTES);
        return mc;
    }

    @Transactional(readOnly = true)
    public List<ProductRanking> getTopProducts(LocalDate fechaInicio, LocalDate fechaFin, Long idSucursal, int limit) {
        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = LocalDate.now();

        Long sucursalId = resolveSucursalId(idSucursal);
        String cacheKey = buildCacheKey(sucursalId, "top-products", fechaInicio, fechaFin);

        @SuppressWarnings("unchecked")
        List<ProductRanking> cached = (List<ProductRanking>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Object[]> rows = detalleNotaVentaRepository.findTopProducts(sucursalId, inicio, fin);
        List<ProductRanking> result = new ArrayList<>();
        int count = 0;
        for (Object[] row : rows) {
            if (count >= limit) break;
            result.add(ProductRanking.builder()
                    .idProducto(((Number) row[0]).longValue())
                    .nombre((String) row[1])
                    .cantidadVendida(((Number) row[2]).intValue())
                    .totalGenerado((BigDecimal) row[3])
                    .build());
            count++;
        }

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_HISTORIC_TTL_MINUTES, TimeUnit.MINUTES);
        return result;
    }

    @Transactional(readOnly = true)
    public List<EmployeeRanking> getEmployeeRanking(LocalDate fechaInicio, LocalDate fechaFin, Long idSucursal) {
        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = LocalDate.now();

        Long sucursalId = resolveSucursalId(idSucursal);
        String cacheKey = buildCacheKey(sucursalId, "employee-ranking", fechaInicio, fechaFin);

        @SuppressWarnings("unchecked")
        List<EmployeeRanking> cached = (List<EmployeeRanking>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Object[]> rows = notaVentaRepository.findEmployeeRanking(sucursalId, inicio, fin);
        List<EmployeeRanking> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(EmployeeRanking.builder()
                    .idEmpleado(((Number) row[0]).longValue())
                    .nombre((String) row[1])
                    .apellido((String) row[2])
                    .totalVentas((BigDecimal) row[3])
                    .numeroVentas(((Number) row[4]).intValue())
                    .build());
        }

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_HISTORIC_TTL_MINUTES, TimeUnit.MINUTES);
        return result;
    }

    private Long resolveSucursalId(Long idSucursal) {
        if (securityUtils.isSuperUser()) {
            return idSucursal;
        }
        return securityUtils.getCurrentSucursalId();
    }

    private String buildCacheKey(Long sucursalId, String type, LocalDate fechaInicio, LocalDate fechaFin) {
        String sufijo = sucursalId != null ? "sucursal:" + sucursalId : "global";
        return CACHE_PREFIX + sufijo + ":" + type + ":" + fechaInicio + ":" + fechaFin;
    }
}
