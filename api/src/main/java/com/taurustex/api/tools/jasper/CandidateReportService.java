package com.taurustex.api.tools.jasper;

import com.taurustex.api.dtos.CandidateReport;
import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
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

    // Si tu charges un fichier .jasper pré-compilé
    private static final String JASPER_PATH = "jasper/candidate_report.jasper";
    private JasperReport compiledJasperReport;

    /**
     * Charge le rapport pré-compilé (.jasper) une seule fois au démarrage de l'application.
     */
    @PostConstruct
    public void init() {
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream(JASPER_PATH)) {
            if (templateStream == null) {
                throw new FileNotFoundException("Le fichier .jasper est introuvable sur le chemin : " + JASPER_PATH);
            }
            // Chargement direct du fichier binaire .jasper (ne nécessite pas javac)
            this.compiledJasperReport = (JasperReport) JRLoader.loadObject(templateStream);
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors du chargement du fichier .jasper : " + JASPER_PATH, e);
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