package br.com.money.service;

import br.com.money.model.Activity;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PdfService {

    @Autowired
    private ActivityService activityService;

    public  ByteArrayInputStream activityPDFReport(List<Activity> activities) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);
            document.open();

            // Add Content to PDF file ->
            Font fontHeader = FontFactory.getFont(String.valueOf(Font.BOLD));
            //fontHeader.setSize(30);
            //Paragraph para = new Paragraph("fnce.", fontHeader);
            Paragraph para = new Paragraph();
            Image png = Image.getInstance("src/main/resources/static/fnce.png");
            png.scaleAbsolute(120F, 60F);
            png.setAlignment(Element.ALIGN_CENTER);
            document.add(png);
            //para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            float[] columnDefinitionSize = { 20F, 30F, 20F, 20F };

            PdfPTable table = new PdfPTable(columnDefinitionSize);
            // Add PDF Table Header ->
            Stream.of("Data", "Descrição", "Valor", "Tipo").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                Font headFont = FontFactory.getFont(String.valueOf(Font.TIMES_ROMAN));
                headFont.setColor(Color.WHITE);
                header.setBackgroundColor(Color.getHSBColor(0.44F, 0.64F, 0.72F));
                header.setFixedHeight(20F);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                header.setBorderWidth(1F);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });

            for (Activity activity : activities) {
                PdfPCell dateCell = new PdfPCell(new Phrase(String.valueOf(new SimpleDateFormat("dd/MM/yyyy").format(activity.getDate()))));
                dateCell.setFixedHeight(20f);
                dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(dateCell);

                PdfPCell description = new PdfPCell(new Phrase(String.valueOf(activity.getDescription())));
                description.setVerticalAlignment(Element.ALIGN_MIDDLE);
                description.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(description);

                PdfPCell value = new PdfPCell(new Phrase(String.valueOf(activity.getValue()).replace(".0", ",00")));
                value.setVerticalAlignment(Element.ALIGN_MIDDLE);
                value.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(value);

                PdfPCell type = new PdfPCell(new Phrase(activity.getType().getTypeValue()));
                type.setVerticalAlignment(Element.ALIGN_MIDDLE);
                type.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(type);
            }
            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            fontHeader = FontFactory.getFont(String.valueOf(Font.BOLD));
            fontHeader.setColor(activityService.balance() < 0 ? Color.RED : Color.getHSBColor(0.44F, 0.70F, 0.60F));
            fontHeader.setSize(20);
            para = new Paragraph("Saldo: R$" + String.valueOf(activityService.balance()).replace(".0", ",00"), fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
