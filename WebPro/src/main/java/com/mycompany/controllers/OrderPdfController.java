package com.mycompany.controllers;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mycompany.models.OrderItem;
import com.mycompany.models.OrderNew;
import com.mycompany.services.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
public class OrderPdfController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders/{orderId}/pdf")
    public void exportOrderPdf(@PathVariable Integer orderId,
                               Authentication authentication,
                               HttpServletResponse response) throws IOException {

        OrderNew order = orderService.getOrderById(orderId).orElse(null);

        if (order == null) {
            response.sendRedirect("/orders");
            return;
        }

        // Security check
        if (authentication == null ||
                (!order.getUserId().equals(authentication.getName()) &&
                        !authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))) {
            response.sendRedirect("/orders");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "inline; filename=hoa-don-" + orderId + ".pdf");

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Use built-in font (no external font file needed)
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(26, 26, 26));
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(51, 51, 51));
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(51, 51, 51));
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(51, 51, 51));
            Font smallFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(128, 128, 128));
            Font accentFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(212, 163, 115));

            // === HEADER ===
            Paragraph storeName = new Paragraph("PCTECH STORE", titleFont);
            storeName.setAlignment(Element.ALIGN_CENTER);
            document.add(storeName);

            Paragraph storeSlogan = new Paragraph("Linh kien may tinh chinh hang", smallFont);
            storeSlogan.setAlignment(Element.ALIGN_CENTER);
            storeSlogan.setSpacingAfter(5);
            document.add(storeSlogan);

            // Divider
            Paragraph divider = new Paragraph("________________________________________________", smallFont);
            divider.setAlignment(Element.ALIGN_CENTER);
            divider.setSpacingAfter(15);
            document.add(divider);

            // Title
            Paragraph invoiceTitle = new Paragraph("HOA DON BAN HANG", accentFont);
            invoiceTitle.setAlignment(Element.ALIGN_CENTER);
            invoiceTitle.setSpacingAfter(5);
            document.add(invoiceTitle);

            Paragraph invoiceNo = new Paragraph("Ma don hang: #" + order.getOrderId(), boldFont);
            invoiceNo.setAlignment(Element.ALIGN_CENTER);
            invoiceNo.setSpacingAfter(20);
            document.add(invoiceNo);

            // === CUSTOMER INFO ===
            Paragraph customerHeader = new Paragraph("THONG TIN KHACH HANG", headerFont);
            customerHeader.setSpacingAfter(8);
            document.add(customerHeader);

            NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

            addInfoRow(document, "Khach hang:", order.getCustomerName() != null ? order.getCustomerName() : "N/A", boldFont, normalFont);
            addInfoRow(document, "Dien thoai:", order.getPhone() != null ? order.getPhone() : "N/A", boldFont, normalFont);
            addInfoRow(document, "Email:", order.getEmail() != null ? order.getEmail() : "N/A", boldFont, normalFont);
            addInfoRow(document, "Dia chi:", order.getShippingAddress() != null ? order.getShippingAddress() : "N/A", boldFont, normalFont);
            addInfoRow(document, "Ngay dat:", order.getCreatedDate() != null ?
                    order.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A", boldFont, normalFont);
            addInfoRow(document, "Thanh toan:", getPaymentMethodText(order.getPaymentMethod()), boldFont, normalFont);

            document.add(new Paragraph(" "));

            // === ORDER ITEMS TABLE ===
            Paragraph itemsHeader = new Paragraph("CHI TIET DON HANG", headerFont);
            itemsHeader.setSpacingAfter(10);
            document.add(itemsHeader);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{5, 40, 20, 25});

            // Table header
            Color headerBg = new Color(245, 247, 250);
            addTableHeader(table, "STT", boldFont, headerBg);
            addTableHeader(table, "San pham", boldFont, headerBg);
            addTableHeader(table, "So luong", boldFont, headerBg);
            addTableHeader(table, "Thanh tien", boldFont, headerBg);

            // Table rows
            int stt = 1;
            for (OrderItem item : order.getOrderItems()) {
                addTableCell(table, String.valueOf(stt++), normalFont);
                addTableCell(table, item.getProduct() != null ? item.getProduct().getTenhh() : "N/A", normalFont);
                addTableCell(table, String.valueOf(item.getQuantity()), normalFont);
                addTableCell(table, currencyFormat.format(item.getPrice().doubleValue() * item.getQuantity()) + " VND", normalFont);
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // === TOTALS ===
            double subtotal = order.getTotalAmount().doubleValue() - 30000;
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(50);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.setWidths(new float[]{50, 50});

            addTotalRow(totalsTable, "Tam tinh:", currencyFormat.format(subtotal) + " VND", normalFont, normalFont);
            addTotalRow(totalsTable, "Phi ship:", "30,000 VND", normalFont, normalFont);

            // Total row with accent
            Font totalLabelFont = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(26, 26, 26));
            Font totalValueFont = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(212, 163, 115));
            addTotalRow(totalsTable, "TONG CONG:", currencyFormat.format(order.getTotalAmount().doubleValue()) + " VND", totalLabelFont, totalValueFont);

            document.add(totalsTable);

            // === FOOTER ===
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Paragraph footer = new Paragraph("Cam on quy khach da mua hang tai PCTech Store!", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            Paragraph footer2 = new Paragraph("Hotline: 1900-xxxx | Email: support@pctech.store", smallFont);
            footer2.setAlignment(Element.ALIGN_CENTER);
            document.add(footer2);

            document.close();

        } catch (DocumentException e) {
            throw new IOException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    private void addInfoRow(Document doc, String label, String value, Font labelFont, Font valueFont) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value, valueFont));
        p.setSpacingAfter(4);
        doc.add(p);
    }

    private void addTableHeader(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(10);
        cell.setBorderColor(new Color(224, 224, 224));
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBorderColor(new Color(240, 240, 240));
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(0);
        labelCell.setPadding(6);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(0);
        valueCell.setPadding(6);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private String getPaymentMethodText(String method) {
        if (method == null) return "COD";
        return switch (method) {
            case "BANK" -> "Chuyen khoan ngan hang";
            case "MOMO" -> "Vi MoMo";
            default -> "Thanh toan khi nhan hang (COD)";
        };
    }
}
