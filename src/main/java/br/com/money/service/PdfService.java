package br.com.money.service;

import br.com.money.model.Activity;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PdfService {
    public  ByteArrayInputStream activityPDFReport(List<Activity> activities) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);
            document.open();

            // Add Content to PDF file ->
            Font fontHeader = FontFactory.getFont(FontFactory.TIMES_BOLD, 18F);
            Paragraph para = new Paragraph("Activity list", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            float[] columnDefinitionSize = { 20F, 30F, 20F, 20F };

            PdfPTable table = new PdfPTable(columnDefinitionSize);
            // Add PDF Table Header ->
            Stream.of("ID", "Description", "Value", "Type").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                Font headFont = FontFactory.getFont(FontFactory.TIMES_BOLD);
                header.setBackgroundColor(Color.GREEN);
                header.setFixedHeight(20F);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                header.setBorderWidth(1F);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });

            for (Activity activity : activities) {
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(activity.getId())));
                idCell.setFixedHeight(20f);
                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                PdfPCell description = new PdfPCell(new Phrase(String.valueOf(activity.getDescription())));
                description.setVerticalAlignment(Element.ALIGN_MIDDLE);
                description.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(description);

                PdfPCell value = new PdfPCell(new Phrase(String.valueOf(activity.getValue())));
                value.setVerticalAlignment(Element.ALIGN_MIDDLE);
                value.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(value);

                PdfPCell type = new PdfPCell(new Phrase(activity.getType().getTypeValue()));
                type.setVerticalAlignment(Element.ALIGN_MIDDLE);
                type.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(type);
            }
            document.add(table);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
