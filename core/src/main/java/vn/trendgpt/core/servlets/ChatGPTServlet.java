package vn.trendgpt.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import vn.trendgpt.core.pojo.chatgpt.ChatGPTRequest;
import vn.trendgpt.core.pojo.chatgpt.ChatGPTResponse;
import vn.trendgpt.core.schedulers.config.ChatGPTConfig;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class, property = {"sling.servlet.paths=/bin/chat"})
@SlingServletResourceTypes(
        resourceTypes = "trendgpt/components/page",
        methods = HttpConstants.METHOD_GET,
        extensions = "json")
@Designate(ocd = ChatGPTConfig.class)
public class ChatGPTServlet extends SlingSafeMethodsServlet {
    private static final String CHATGPT_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final HttpClient client = HttpClients.createDefault();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String openAISecretKey;

    @Activate
    @Modified
    protected void active(ChatGPTConfig config) {
        openAISecretKey = config.openAISecretKey();
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String prompt = request.getParameter("prompt");

        String requestBody = MAPPER.writeValueAsString(new ChatGPTRequest(prompt, "gpt-3.5-turbo", "user"));
        HttpPost httpPost = new HttpPost(CHATGPT_API_ENDPOINT);
        httpPost.addHeader("Authorization", "Bearer " + openAISecretKey);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(requestBody));

        HttpResponse httpResponse = client.execute(httpPost);

        ChatGPTResponse chatGptResponse = MAPPER.readValue(EntityUtils.toString(httpResponse.getEntity()), ChatGPTResponse.class);
        String message = chatGptResponse.getChoices().get(0).getMessage().getContent();

        response.getWriter().write(message);
    }
}
