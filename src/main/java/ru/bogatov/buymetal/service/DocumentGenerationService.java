package ru.bogatov.buymetal.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import ru.bogatov.buymetal.BuymetalApplication;
import ru.bogatov.buymetal.entity.Order;
import ru.bogatov.buymetal.repository.OrderRepository;
import ru.bogatov.buymetal.service.file.FileService;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentGenerationService {

    private final Configuration freeMarkerConfig;

    private final OrderRepository orderRepository;

    private final FileService fileService;

    public Void generateDocument(UUID orderId) {
        String rawHtml = "error";
        Order order = orderRepository.findById(orderId).get();
        try {
            Template template = freeMarkerConfig.getTemplate("order_document.ftl");
            Map<String, Object> context = new HashMap<>();
            context.put("order", order);
            context.put("application", order.getApplication());
            context.put("applicationResponse", order.getResponse());
            context.put("customer", order.getApplication().getCustomer());
            context.put("supplier", order.getResponse().getSupplier());
            context.put("orderNumber", order.getId().toString().substring(0,8));
            StringWriter writer = new StringWriter();
            template.process(context, writer);
            rawHtml = writer.toString();
        } catch (RuntimeException | IOException | TemplateException e) {
            log.error(e.getMessage());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withW3cDocument(parseHtml5(rawHtml), null);
        builder.useFastMode();
        builder.useFont(new File(BuymetalApplication.class.getClassLoader().getResource("templates/ArialRegular.ttf").getFile()), "Arial");
        builder.toStream(outputStream);

        try {
            builder.run();
        } catch (Exception e) {
            throw new RuntimeException("Cannot render PDF.", e);
        }
        if (order.getDocument() == null) {
            order.setDocument(fileService.saveFile(outputStream.toByteArray()));
        } else {
            fileService.updateFile(order.getDocument().getId(), outputStream.toByteArray());
        }
        orderRepository.save(order);
        return null;
    }

    private static Document parseHtml5(String html) {
        return new W3CDom().fromJsoup(Jsoup.parse(html));
    }

}
