package com.taurustex.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateReport {
    String title;
    String description;
    String year;
    InputStream leftLogo;
    InputStream rightLogo;
    List<CandidateExportDto> candidates;

    public Map<String, Object> toReportParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", this.title);
        parameters.put("description", this.description);
        parameters.put("year", this.year);
        parameters.put("leftLogo", this.leftLogo);
        parameters.put("rightLogo", this.rightLogo);
        return parameters;
    }
}
