package com.eroom.approval.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Controller
public class PdfController {

    private final TemplateEngine templateEngine;
//    final JakartaServletWebApplication application =
//    	    JakartaServletWebApplication.buildApplication(null);

    // 생성자 주입 (Spring Boot에서 자동 주입됩니다)
    public PdfController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> generatePdf(HttpServletRequest request,
                                                           HttpServletResponse response,
                                                           Locale locale) {
        try {
            // 1. 템플릿에 전달할 변수 준비
            Map<String, Object> variables = new HashMap<>();
            variables.put("title", "PDF 예제 제목");
            variables.put("message", "이것은 OpenHTMLtoPDF를 사용한 예제 PDF 내용입니다.");

            // 2. Thymeleaf 처리 컨텍스트 생성 (WebContext 사용)
//            JakartaServletWebApplication application =
//            	    JakartaServletWebApplication.buildApplication(null);
            JakartaServletWebApplication application =
                    JakartaServletWebApplication.buildApplication(request.getServletContext());
            IWebExchange webExchange = application.buildExchange(request, response);
            IWebRequest webRequest = webExchange.getRequest();
			WebContext context = new WebContext(webExchange, request.getLocale());

            context.setVariables(variables);

            // 3. 템플릿 처리: pdfTemplate.html 로부터 HTML 콘텐츠 생성
//            String htmlContent = templateEngine.process("pdfTemplate", context);
            String htmlContent = templateEngine.process("approval/detail", context);

            // 4. HTML 콘텐츠를 PDF로 변환
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, "");  // 두번째 인자는 Base URL (필요시 설정)
            builder.toStream(os);
            builder.run();

            // 5. PDF 데이터를 InputStreamResource로 변환하여 반환
            ByteArrayInputStream bis = new ByteArrayInputStream(os.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=sample.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
