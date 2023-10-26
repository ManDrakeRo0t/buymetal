package ru.bogatov.buymetal.service;

import freemarker.core.ParseException;
import freemarker.template.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentGenerationService {

    private final Configuration freeMarkerConfig;

    public String generateDocument(UUID orderId) {

        try {
            Template template = freeMarkerConfig.getTemplate("order_document.html");
            Map<String, Object> context = new HashMap<>();
            context.put("name", "Danila");
            StringWriter writer = new StringWriter();
            template.process(context, writer);
            return writer.toString();
        } catch (RuntimeException | IOException | TemplateException e) {
            log.error(e.getMessage());
        }
        return "error";
    }

}
