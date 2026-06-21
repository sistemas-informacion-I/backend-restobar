package org.restobar.gaira.modulo_electronico.entity;

import jakarta.persistence.*;
import lombok.*;
import org.restobar.gaira.modulo_operaciones.entity.Mesa;

@Entity
@Table(name = "reserva_mesa", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaMesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_mesa")
    private Long idReservaMesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;
}
