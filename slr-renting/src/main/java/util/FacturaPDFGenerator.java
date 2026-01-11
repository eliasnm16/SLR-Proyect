package util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import dto.AlquilerDTO;
import dto.CocheDTO;

public class FacturaPDFGenerator {

    public static void generarFactura(
            int idAlquiler,
            AlquilerDTO alquiler,
            CocheDTO coche,
            boolean choferSolicitado,
            double precioDia,
            long dias,
            double subtotal,
            double descuentoEuros,
            double total
    ) throws IOException {

        // Crear carpeta si no existe
        File dir = new File("facturas");
        if (!dir.exists()) dir.mkdirs();

        String nombreArchivo = "facturas/factura_alquiler_" + idAlquiler + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.newLineAtOffset(50, 750);
                content.showText("FACTURA DE ALQUILER");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, 720);

                content.showText("ID Alquiler: " + idAlquiler); content.newLineAtOffset(0, -20);
                content.showText("Fecha emisión: " + LocalDate.now()); content.newLineAtOffset(0, -20);
                content.showText("Cliente (NIF): " + alquiler.getNif_nie()); content.newLineAtOffset(0, -30);

                content.showText("Coche: " + coche.getMarca() + " " + coche.getModelo()); content.newLineAtOffset(0, -20);
                content.showText("Bastidor: " + coche.getBastidor()); content.newLineAtOffset(0, -20);
                content.showText("Matrícula: " + coche.getMatricula()); content.newLineAtOffset(0, -30);

                content.showText("Fecha inicio: " + alquiler.getFechaInicio()); content.newLineAtOffset(0, -20);
                content.showText("Fecha fin: " + alquiler.getFechaFin()); content.newLineAtOffset(0, -20);
                content.showText("Días: " + dias); content.newLineAtOffset(0, -30);

                content.showText("Precio por día: " + String.format("%.2f €", precioDia)); content.newLineAtOffset(0, -20);
                content.showText("Subtotal: " + String.format("%.2f €", subtotal)); content.newLineAtOffset(0, -20);
                content.showText("Descuento: " + String.format("%.2f €", descuentoEuros)); content.newLineAtOffset(0, -20);
                content.showText("Chofer: " + (choferSolicitado ? "Sí" : "No")); content.newLineAtOffset(0, -20);

                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.showText("TOTAL: " + String.format("%.2f €", total));

                content.endText();
            }

            document.save(nombreArchivo);
        }
    }
}
