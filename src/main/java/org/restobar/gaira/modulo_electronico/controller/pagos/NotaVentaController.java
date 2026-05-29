package org.restobar.gaira.modulo_electronico.controller.pagos;

import org.restobar.gaira.modulo_comercial.service.PdfService;
import org.restobar.gaira.modulo_electronico.service.pagos.NotaVentaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notas-venta")
@RequiredArgsConstructor
public class NotaVentaController {

    @Qualifier("electronicoNotaVentaService")
    private final NotaVentaService notaVentaService;
    private final PdfService pdfService;

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<Map<String, Object>>> obtenerMisPedidos(Authentication authentication) {
        return ResponseEntity.ok(notaVentaService.obtenerMisPedidos(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notaVentaService.obtenerPorId(id));
    }

    @GetMapping("/{id}/pdf")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> descargarFacturaPdf(@PathVariable Long id) {
        byte[] pdfBytes = pdfService.generarFacturaPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("Factura-" + id + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}