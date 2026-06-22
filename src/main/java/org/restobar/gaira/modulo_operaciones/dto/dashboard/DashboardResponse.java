package org.restobar.gaira.modulo_operaciones.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private KpiDTO kpis;
    private List<SalesPoint> salesEvolution;
    private List<CategorySales> salesByCategory;
    private MonthComparison monthComparison;
    private List<ProductRanking> topProducts;
    private List<EmployeeRanking> employeeRanking;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiDTO {
        private BigDecimal ventasDelDia;
        private int numeroTransacciones;
        private BigDecimal ticketPromedio;
        private BigDecimal totalGanancia;
        private BigDecimal margenGanancia;
        private int comandasActivas;
        private int reservasDelDia;
        private int alertasStockCritico;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesPoint {
        private LocalDate fecha;
        private BigDecimal total;
        private int count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySales {
        private String categoria;
        private BigDecimal total;
        private double porcentaje;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthComparison {
        private BigDecimal mesActual;
        private BigDecimal mesAnterior;
        private BigDecimal variacion;
        private String periodoActual;
        private String periodoAnterior;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRanking {
        private Long idProducto;
        private String nombre;
        private int cantidadVendida;
        private BigDecimal totalGenerado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeRanking {
        private Long idEmpleado;
        private String nombre;
        private String apellido;
        private BigDecimal totalVentas;
        private int numeroVentas;
    }
}
