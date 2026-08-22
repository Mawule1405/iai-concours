package com.taurustex.api.tools.jasper;

import com.taurustex.api.dtos.CandidateReport;
import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Service
public class CandidateReportService {

    private static final String JRXML_PATH = "jasper/candidate_report.jrxml";
    private JasperReport compiledJasperReport;

    /**
     * Charge et compile le rapport une seule fois au démarrage de l'application.
     */
    @PostConstruct
    public void init() {
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream(JRXML_PATH)) {
            if (templateStream == null) {
                throw new FileNotFoundException("Le fichier JRXML est introuvable sur le chemin : " + JRXML_PATH);
            }
            this.compiledJasperReport = JasperCompileManager.compileReport(templateStream);
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la compilation du fichier JRXML : " + JRXML_PATH, e);
        }
    }

    public byte[] generateReportPdf(CandidateReport dto) {
        if (dto == null) {
            return new byte[0];
        }
        return generateReportPdfV10(Collections.singletonList(dto));
    }

    public byte[] generateReportPdfV10(List<CandidateReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return new byte[0];
        }

        try {
            List<JasperPrint> jasperPrints = new ArrayList<>();

            for (CandidateReport report : reports) {

                Map<String, Object> parameters = new HashMap<>();

                // Paramètres généraux du rapport
                parameters.put("title", report.getTitle());
                parameters.put("description", report.getDescription());
                parameters.put("year", report.getYear());

                // Conversion sécurisée des Flux Logos
                parameters.put("leftLogo", copyInputStream(report.getLeftLogo()));
                parameters.put("rightLogo", copyInputStream(report.getRightLogo()));

                // Source de données = Liste des candidats
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                        report.getCandidates() != null ? report.getCandidates() : Collections.emptyList()
                );

                // Remplissage du rapport
                JasperPrint jasperPrint = JasperFillManager.fillReport(this.compiledJasperReport, parameters, dataSource);
                jasperPrints.add(jasperPrint);
            }

            // Exportation au format PDF
            if (jasperPrints.size() == 1) {
                return JasperExportManager.exportReportToPdf(jasperPrints.get(0));
            } else {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                exporter.exportReport();

                return outputStream.toByteArray();
            }

        } catch (Throwable t) {
            // Capture à la fois les Exception et les Error (ex: NoClassDefFoundError)
            throw new RuntimeException("Erreur critique lors de la génération du rapport PDF : " + t.getMessage(), t);
        }
    }
    /**
     * Duplique un InputStream sous forme de ByteArrayInputStream pour qu'il puisse être réutilisé en toute sécurité par Jasper.
     */
    private InputStream copyInputStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            byte[] bytes = inputStream.readAllBytes();
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            return null;
        }
    }
}