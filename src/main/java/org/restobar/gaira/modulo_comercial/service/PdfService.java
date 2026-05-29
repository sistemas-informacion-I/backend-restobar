package org.restobar.gaira.modulo_comercial.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.repository.NotaVentaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final NotaVentaRepository notaVentaRepository;

    private static final Color WINE_COLOR = new Color(172, 17, 26);
    private static final Color WINE_LIGHT = new Color(251, 202, 208);
    private static final Color GRAY_BG = new Color(243, 244, 246);
    private static final Color TEXT_DARK = new Color(31, 41, 55);
    private static final Color TEXT_GRAY = new Color(107, 114, 128);

    public byte[] generarFacturaPdf(Long idNotaVenta) {
        NotaVenta nv = notaVentaRepository.findById(idNotaVenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Fuente base
            Font fontTitle = new Font(Font.HELVETICA, 22, Font.BOLD, WINE_COLOR);
            Font fontSubtitle = new Font(Font.HELVETICA, 10, Font.NORMAL, TEXT_GRAY);
            Font fontSection = new Font(Font.HELVETICA, 12, Font.BOLD, TEXT_DARK);
            Font fontLabel = new Font(Font.HELVETICA, 9, Font.NORMAL, TEXT_GRAY);
            Font fontValue = new Font(Font.HELVETICA, 9, Font.BOLD, TEXT_DARK);
            Font fontTableHeader = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
            Font fontTableCell = new Font(Font.HELVETICA, 9, Font.NORMAL, TEXT_DARK);
            Font fontTotal = new Font(Font.HELVETICA, 11, Font.BOLD, WINE_COLOR);
            Font fontBadge = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);

            // Header
            Paragraph title = new Paragraph("Restobar La Gaira", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Factura de Venta", fontSubtitle);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Info principal
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(15);
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            infoTable.getDefaultCell().setPadding(4);

            infoTable.addCell(createLabelCell("Número de comanda:", fontLabel));
            infoTable.addCell(createValueCell(nv.getComanda() != null ? nv.getComanda().getNumeroComanda() : "N/A", fontValue));

            infoTable.addCell(createLabelCell("Número de factura:", fontLabel));
            String invoiceNum = nv.getTransaccionesOnline() != null && !nv.getTransaccionesOnline().isEmpty()
                    ? nv.getTransaccionesOnline().get(0).getNumeroTransaccion()
                    : "N/A";
            infoTable.addCell(createValueCell(invoiceNum, fontValue));

            infoTable.addCell(createLabelCell("Fecha de emisión:", fontLabel));
            infoTable.addCell(createValueCell(nv.getFechaEmision() != null
                    ? nv.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "-", fontValue));

            infoTable.addCell(createLabelCell("Fecha de pago:", fontLabel));
            infoTable.addCell(createValueCell(nv.getFechaPago() != null
                    ? nv.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "-", fontValue));

            infoTable.addCell(createLabelCell("Estado:", fontLabel));
            PdfPCell estadoCell = new PdfPCell(new Phrase("  " + nv.getEstado() + "  ", fontBadge));
            estadoCell.setBorder(Rectangle.NO_BORDER);
            estadoCell.setPadding(4);
            estadoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            if ("PAGADA".equals(nv.getEstado())) {
                estadoCell.setBackgroundColor(new Color(16, 185, 129));
            } else {
                estadoCell.setBackgroundColor(new Color(239, 68, 68));
            }
            infoTable.addCell(estadoCell);

            document.add(infoTable);

            // Separador
            document.add(new Paragraph(" "));

            // Datos del cliente
            Paragraph clienteTitle = new Paragraph("Datos del Cliente", fontSection);
            clienteTitle.setSpacingAfter(8);
            document.add(clienteTitle);

            PdfPTable clienteTable = new PdfPTable(2);
            clienteTable.setWidthPercentage(100);
            clienteTable.setSpacingAfter(15);
            clienteTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            clienteTable.getDefaultCell().setPadding(8);
            clienteTable.getDefaultCell().setBackgroundColor(GRAY_BG);

            String clienteNombre = nv.getCliente() != null && nv.getCliente().getUsuario() != null
                    ? nv.getCliente().getUsuario().getNombre() + " " + nv.getCliente().getUsuario().getApellido()
                    : "Cliente General";
            String clienteEmail = nv.getCliente() != null && nv.getCliente().getUsuario() != null
                    ? nv.getCliente().getUsuario().getCorreo()
                    : "-";
            String nit = nv.getNitCliente() != null ? nv.getNitCliente() : "-";

            clienteTable.addCell(createLabelCell("Cliente:", fontLabel));
            clienteTable.addCell(createValueCell(clienteNombre, fontValue));
            clienteTable.addCell(createLabelCell("Correo:", fontLabel));
            clienteTable.addCell(createValueCell(clienteEmail, fontValue));
            clienteTable.addCell(createLabelCell("NIT:", fontLabel));
            clienteTable.addCell(createValueCell(nit, fontValue));

            document.add(clienteTable);

            // Tabla de items
            Paragraph itemsTitle = new Paragraph("Detalle del Pedido", fontSection);
            itemsTitle.setSpacingAfter(8);
            document.add(itemsTitle);

            PdfPTable itemsTable = new PdfPTable(new float[]{50f, 15f, 20f, 15f});
            itemsTable.setWidthPercentage(100);
            itemsTable.setSpacingAfter(15);

            // Headers
            String[] headers = {"Producto", "Cant.", "P. Unitario", "Subtotal"};
            for (String h : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(h, fontTableHeader));
                headerCell.setBackgroundColor(WINE_COLOR);
                headerCell.setPadding(8);
                headerCell.setBorderColor(WINE_COLOR);
                itemsTable.addCell(headerCell);
            }

            // Items
            if (nv.getDetalleNotaVentas() != null) {
                boolean even = false;
                for (DetalleNotaVenta det : nv.getDetalleNotaVentas()) {
                    Color rowColor = even ? new Color(249, 250, 251) : Color.WHITE;
                    String prodName = det.getProductoFinal() != null ? det.getProductoFinal().getNombre() : "Producto";

                    PdfPCell c1 = new PdfPCell(new Phrase(prodName, fontTableCell));
                    c1.setBackgroundColor(rowColor);
                    c1.setPadding(6);
                    c1.setBorderColor(new Color(229, 231, 235));
                    itemsTable.addCell(c1);

                    PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(det.getCantidad()), fontTableCell));
                    c2.setBackgroundColor(rowColor);
                    c2.setPadding(6);
                    c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c2.setBorderColor(new Color(229, 231, 235));
                    itemsTable.addCell(c2);

                    PdfPCell c3 = new PdfPCell(new Phrase(formatBs(det.getPrecioUnitario()), fontTableCell));
                    c3.setBackgroundColor(rowColor);
                    c3.setPadding(6);
                    c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    c3.setBorderColor(new Color(229, 231, 235));
                    itemsTable.addCell(c3);

                    PdfPCell c4 = new PdfPCell(new Phrase(formatBs(det.getSubtotal()), fontTableCell));
                    c4.setBackgroundColor(rowColor);
                    c4.setPadding(6);
                    c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    c4.setBorderColor(new Color(229, 231, 235));
                    itemsTable.addCell(c4);

                    even = !even;
                }
            }

            document.add(itemsTable);

            // Totales
            PdfPTable totalesTable = new PdfPTable(2);
            totalesTable.setWidthPercentage(40);
            totalesTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalesTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            totalesTable.getDefaultCell().setPadding(4);

            addTotalRow(totalesTable, "Subtotal:", nv.getSubtotal(), fontLabel, fontValue);
            addTotalRow(totalesTable, "Impuesto:", nv.getImpuesto(), fontLabel, fontValue);
            addTotalRow(totalesTable, "Propina:", nv.getPropina(), fontLabel, fontValue);
            if (nv.getDescuento() != null && nv.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
                addTotalRow(totalesTable, "Descuento:", nv.getDescuento(), fontLabel, fontValue);
            }

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL:", fontTotal));
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setPadding(6);
            totalesTable.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(formatBs(nv.getTotal()), fontTotal));
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setPadding(6);
            totalesTable.addCell(totalValueCell);

            document.add(totalesTable);

            // Footer
            Paragraph footer = new Paragraph("Gracias por su preferencia — Restobar La Gaira", fontSubtitle);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el PDF: " + e.getMessage());
        }
    }

    private PdfPCell createLabelCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell createValueCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(4);
        return cell;
    }

    private void addTotalRow(PdfPTable table, String label, BigDecimal value, Font labelFont, Font valueFont) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, labelFont));
        lCell.setBorder(Rectangle.NO_BORDER);
        lCell.setPadding(4);
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(formatBs(value), valueFont));
        vCell.setBorder(Rectangle.NO_BORDER);
        vCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        vCell.setPadding(4);
        table.addCell(vCell);
    }

    private String formatBs(BigDecimal amount) {
        if (amount == null) return "Bs. 0,00";
        return "Bs. " + amount.setScale(2, RoundingMode.HALF_UP)
                .toString().replace(".", ",");
    }
}
