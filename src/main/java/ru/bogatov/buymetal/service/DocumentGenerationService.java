package ru.bogatov.buymetal.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.*;
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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DocumentGenerationService {

    private final Configuration freeMarkerConfig;
    private final OrderRepository orderRepository;
    private final FileService fileService;
    private File arialFont;
    private Template documentTemplate;
    public DocumentGenerationService(Configuration freeMarkerConfig, OrderRepository orderRepository, FileService fileService) {
        this.freeMarkerConfig = freeMarkerConfig;
        this.orderRepository = orderRepository;
        this.fileService = fileService;

        log.info("Loading resources...");

        try {
            log.info("Loading template");
            documentTemplate = freeMarkerConfig.getTemplate("order_document.ftl");
            log.info("Loading font");

            URL fontUrl = BuymetalApplication.class.getClassLoader().getResource("templates/ArialRegular.ttf");
//            URL fontUrl = BuymetalApplication.class.getClassLoader().getResource("templates/order_document.ftl");
            log.info(fontUrl.toString());
            arialFont = new File(fontUrl.getFile());
//            arialFont = streamToFile(getClass().getClassLoader().getResourceAsStream("templates/ArialRegular.ttf"));
            log.info("File : {}", arialFont);
            log.info("File exists: {}", arialFont.exists());
            if (!arialFont.exists()) {
                log.info("loading from root");
                arialFont = new File("/ArialRegular.ttf");
                log.info("File exists: {}", arialFont.exists());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Loading resources... done");
    }


    public Void generateDocument(UUID orderId) {
        String rawHtml = "error";
        Order order = orderRepository.findById(orderId).get();
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("order", order);
            context.put("application", order.getApplication());
            context.put("applicationResponse", order.getResponse());
            context.put("customer", order.getApplication().getCustomer());
            context.put("supplier", order.getResponse().getSupplier());
            context.put("orderNumber", order.getId().toString().substring(0,8));
            StringWriter writer = new StringWriter();
            documentTemplate.process(context, writer);
            rawHtml = writer.toString();
        } catch (RuntimeException | IOException | TemplateException e) {
            log.error(e.getMessage());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withW3cDocument(parseHtml5(rawHtml), null);
        builder.useFastMode();
        builder.useFont(arialFont, "Arial");
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

    public static File streamToFile(InputStream in) {
        if (in == null) {
            return null;
        }

        try {
            File f = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            f.deleteOnExit();

            FileOutputStream out = new FileOutputStream(f);
            byte[] buffer = new byte[1024];

            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            return f;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
