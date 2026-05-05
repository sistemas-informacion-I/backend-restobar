package org.restobar.gaira.modulo_acceso.repository;

import java.time.LocalDateTime;

import org.restobar.gaira.modulo_acceso.entity.LogAuditoria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import jakarta.persistence.criteria.JoinType;

public interface LogAuditoriaRepository
                extends JpaRepository<LogAuditoria, Long>, JpaSpecificationExecutor<LogAuditoria> {

        static Specification<LogAuditoria> porTabla(String tabla) {
                return (root, query, cb) -> tabla == null || tabla.isBlank()
                                ? cb.conjunction()
                                : cb.like(cb.lower(root.get("tabla")), "%" + tabla.toLowerCase() + "%");
        }

        static Specification<LogAuditoria> porOperacion(String operacion) {
                return (root, query, cb) -> operacion == null || operacion.isBlank()
                                ? cb.conjunction()
                                : cb.equal(cb.lower(root.get("operacion")), operacion.toLowerCase());
        }

        static Specification<LogAuditoria> porUsuario(Long idUsuario) {
                return (root, query, cb) -> idUsuario == null
                                ? cb.conjunction()
                                : cb.equal(root.join("usuario", JoinType.LEFT).get("idUsuario"), idUsuario);
        }

        static Specification<LogAuditoria> porSucursal(Long idSucursal) {
                return (root, query, cb) -> idSucursal == null
                                ? cb.conjunction()
                                : cb.equal(root.get("idSucursal"), idSucursal);
        }

        static Specification<LogAuditoria> porRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
                return (root, query, cb) -> {
                        if (desde != null && hasta != null) {
                                return cb.between(root.get("fechaOperacion"), desde, hasta);
                        } else if (desde != null) {
                                return cb.greaterThanOrEqualTo(root.get("fechaOperacion"), desde);
                        } else if (hasta != null) {
                                return cb.lessThanOrEqualTo(root.get("fechaOperacion"), hasta);
                        }
                        return cb.conjunction();
                };
        }

        static Specification<LogAuditoria> buildFrom(
                        String tabla, String operacion, Long idUsuario, Long idSucursal,
                        LocalDateTime desde, LocalDateTime hasta) {
                return porTabla(tabla)
                                .and(porOperacion(operacion))
                                .and(porUsuario(idUsuario))
                                .and(porSucursal(idSucursal))
                                .and(porRangoFechas(desde, hasta));
        }
}