    package com.email_writer.service;


    import com.email_writer.entity.EmailRequest;
    import org.springframework.stereotype.Service;

    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.web.reactive.function.client.WebClient;

    import java.util.Map;


    import com.email_writer.entity.EmailRequest;
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.web.reactive.function.client.WebClient;
    import java.util.Map;

    @Service
    public class EmailGeneratorService {

        private final WebClient webClient;

        @Value("${gemini.api.url}")
        private String geminiApiUrl;

        @Value("${gemini.api.key}")
        private String geminiApiKey;

        public EmailGeneratorService(WebClient.Builder webClientBuilder) {
            this.webClient = webClientBuilder.build();
        }

        public String generateEmailReply(EmailRequest emailRequest) {
            String prompt = buildPrompt(emailRequest, "Generate a professional email reply for the following email content. Please don't generate a subject line.");
            return fetchGeminiResponse(prompt);
        }

        public String summarizeEmail(EmailRequest emailRequest) {
            String prompt = buildPrompt(emailRequest, "Summarize the following email conversation in a few sentences.");
            return fetchGeminiResponse(prompt);
        }

        public String extractKeyActionPoints(EmailRequest emailRequest) {
            String prompt = buildPrompt(emailRequest, "Extract the key action points from the following email conversation. Present them as a bullet-point list:");
            return fetchGeminiResponse(prompt);
        }

        public String analyzeEmailSentiment(EmailRequest emailRequest) {
            String prompt = buildPrompt(emailRequest, "Analyze the sentiment of the following email (positive, neutral, or negative):");
            return fetchGeminiResponse(prompt);
        }

        public String categorizeEmail(EmailRequest emailRequest) {
            String prompt = buildPrompt(emailRequest, "Categorize the following email into one of the categories: Inquiry, Complaint, Task, Meeting, Social, Promotion, or Other:");
            return fetchGeminiResponse(prompt);
        }

        public String extractCalendarEvent(EmailRequest emailRequest) {
            String prompt = buildPrompt(emailRequest, "Extract any dates, times, and event details from the following email content. Present them as a structured event description:");
            return fetchGeminiResponse(prompt);
        }

        private String fetchGeminiResponse(String prompt) {
            Map<String, Object> requestBody = Map.of(
                    "contents", new Object[]{
                            Map.of("parts", new Object[]{
                                    Map.of("text", prompt)
                            })
                    }
            );

            String response = webClient.post()
                    .uri(geminiApiUrl + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractResponseContent(response);
        }

        private String extractResponseContent(String response) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response);
                return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            } catch (Exception e) {
                return "Error processing request: " + e.getMessage();
            }
        }

        private String buildPrompt(EmailRequest emailRequest, String instruction) {
            StringBuilder prompt = new StringBuilder();
            prompt.append(instruction).append("\n");
            prompt.append("Original email: \n").append(emailRequest.getEmailContent());
            return prompt.toString();
        }
    }
