package com.eroom.approval.controller;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PdfControllerTest {

//    private final TemplateEngine templateEngine;
//
//    @GetMapping("/pdf2")
//    public ResponseEntity<InputStreamResource> generatePdf(HttpServletRequest request,
//                                                           HttpServletResponse response,
//                                                           Locale locale) {
//        try {
//            // 1. 템플릿에 전달할 변수 준비
//            Map<String, Object> variables = new HashMap<>();
//            variables.put("title", "PDF 예제 제목");
//            variables.put("message", "이것은 OpenHTMLtoPDF를 사용한 예제 PDF 내용입니다.");
//
//            // 2. Thymeleaf 처리 컨텍스트 생성 (WebContext 사용)
//            JakartaServletWebApplication application =
//                    JakartaServletWebApplication.buildApplication(request.getServletContext());
//            IWebExchange webExchange = application.buildExchange(request, response);
//            IWebRequest webRequest = webExchange.getRequest();
//            WebContext context = new WebContext(webExchange, request.getLocale());
//            context.setVariables(variables);
//
//            // 3. 템플릿 처리: detail 페이지를 통해 전체 HTML 콘텐츠 생성
//            String htmlContent = templateEngine.process("approval/detail", context);
//
//            // 4. Jsoup로 HTML 전체를 파싱하고, <div class="content"> 태그 안쪽만 추출 (XML 출력 설정)
//            org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(htmlContent);
//            document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
//            org.jsoup.nodes.Element contentDiv = document.select("div.content").first();
//            String contentHtml = (contentDiv != null) ? contentDiv.html() : "";
//
//            // 5. 인라인으로 추가할 폰트 적용 CSS (예: 기본 폰트 설정)
//            String fontCss = "body { font-family: 'Malgun Gothic', sans-serif; }";
//            
//            // 6. 클래스패스 내 로컬 CSS 파일들을 읽어오기 (여러 CSS 파일을 하나로 합치기)
//            // 경로는 프로젝트 구조에 맞게 조정하세요.
//            List<String> cssPaths = Arrays.asList(
//                "static/assets/css/theme.min.css",
//                "static/assets/css/user.min.css",
//                "static/vendors/simplebar/simplebar.min.css",
//                "static/vendors/leaflet/leaflet.css",
//                "static/vendors/leaflet.markercluster/MarkerCluster.css",
//                "static/vendors/leaflet.markercluster/MarkerCluster.Default.css"
//            );
//            StringBuilder cssBuilder = new StringBuilder();
//            cssBuilder.append(fontCss);
//            for (String path : cssPaths) {
//                try {
//                    ClassPathResource cssResource = new ClassPathResource(path);
//                    String cssContent = IOUtils.toString(cssResource.getInputStream(), StandardCharsets.UTF_8);
//                    cssBuilder.append("\n").append(cssContent);
//                } catch (Exception e) {
//                    System.err.println("CSS 파일 로드 실패: " + path);
//                    e.printStackTrace();
//                }
//            }
//            // CDATA 블록으로 감싸서 인라인 style 태그 생성 (XML 파서 이스케이프 문제 회피)
//            String allCss = "<style><![CDATA[" + cssBuilder.toString() + "]]></style>";
//
//            // 7. 최종 HTML 구성: <head>에 인라인된 모든 CSS 추가, <body>에 추출된 contentHtml 삽입
//            String finalHtml = "<html><head>" + allCss + "</head><body>" + contentHtml + "</body></html>";
//
//            // 8. PDF 변환을 위한 PdfRendererBuilder 설정
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            PdfRendererBuilder builder = new PdfRendererBuilder();
//
//            // ★ 한글 폰트 등록 (배포 환경에서도 접근 가능하도록 ClassPathResource 활용)
//            // 일반 폰트 (malgun.ttf)
//            ClassPathResource regFontResource = new ClassPathResource("fonts/malgun.ttf");
//            File tempRegFont = File.createTempFile("malgun", ".ttf");
//            try (InputStream is = regFontResource.getInputStream()) {
//                FileUtils.copyInputStreamToFile(is, tempRegFont);
//            }
//            builder.useFont(tempRegFont, "Malgun Gothic", 400, PdfRendererBuilder.FontStyle.NORMAL, true);
//
//            // 볼드 폰트 (malgunbd.ttf)
//            ClassPathResource boldFontResource = new ClassPathResource("fonts/malgunbd.ttf");
//            File tempBoldFont = File.createTempFile("malgunbd", ".ttf");
//            try (InputStream is = boldFontResource.getInputStream()) {
//                FileUtils.copyInputStreamToFile(is, tempBoldFont);
//            }
//            builder.useFont(tempBoldFont, "Malgun Gothic", 700, PdfRendererBuilder.FontStyle.NORMAL, true);
//
//            // 9. 후처리된 최종 HTML(finalHtml)을 PDF로 변환 (Base URL 필요 시 설정)
//            builder.withHtmlContent(finalHtml, "");
//            builder.toStream(os);
//            builder.run();
//
//            // 10. 생성된 PDF 데이터를 InputStreamResource로 변환하여 반환
//            ByteArrayInputStream bis = new ByteArrayInputStream(os.toByteArray());
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "inline; filename=sample.pdf");
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(new InputStreamResource(bis));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//    }
}
