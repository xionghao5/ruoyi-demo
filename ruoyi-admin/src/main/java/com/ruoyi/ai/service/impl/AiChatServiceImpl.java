package com.ruoyi.ai.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ruoyi.common.core.domain.entity.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ai.service.AiChatService;
import com.ruoyi.ai.service.IAiConversationService;
import com.ruoyi.ai.service.IAiMessageService;
import com.ruoyi.ai.domain.AiConversation;
import com.ruoyi.ai.domain.AiMessage;
import com.ruoyi.common.utils.ShiroUtils;

/**
 * AI智能问答服务实现类
 * 
 * @author ruoyi
 */
@Service
public class AiChatServiceImpl implements AiChatService
{
    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    /** SSE超时时间（5分钟） */
    private static final long SSE_TIMEOUT = 5 * 60 * 1000L;

    /** 异步线程池，用于流式传输 */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${ai.model:gpt-3.5-turbo}")
    private String model;

    @Autowired
    private IAiConversationService conversationService;

    @Autowired
    private IAiMessageService messageService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public AjaxResult chat(String message)
    {
        if (apiKey == null || apiKey.trim().isEmpty())
        {
            return AjaxResult.error("AI服务未配置，请先配置API Key");
        }

        try
        {
            String url = baseUrl + "/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
            {
                JSONObject jsonObject = JSON.parseObject(response.getBody());
                JSONArray choices = jsonObject.getJSONArray("choices");
                if (choices != null && !choices.isEmpty())
                {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject messageObj = firstChoice.getJSONObject("message");
                    String content = messageObj.getString("content");
                    return AjaxResult.success(content);
                }
                return AjaxResult.error("AI返回数据格式异常");
            }
            else
            {
                return AjaxResult.error("AI服务请求失败，状态码：" + response.getStatusCodeValue());
            }
        }
        catch (Exception e)
        {
            log.error("AI问答请求异常", e);
            return AjaxResult.error("AI服务调用失败：" + e.getMessage());
        }
    }

    @Override
    public SseEmitter streamChat(String message, Long conversationId)
    {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // 获取当前登录用户
        SysUser currentUser = ShiroUtils.getSysUser();
        String loginName = currentUser != null ? currentUser.getLoginName() : "";
        Long userId = currentUser != null ? currentUser.getUserId() : 0L;

        // 创建或获取对话
        final Long convId;
        if (conversationId == null)
        {
            // 新建对话，标题取问题前50字符
            AiConversation conversation = new AiConversation();
            conversation.setTitle(message.length() > 50 ? message.substring(0, 50) + "..." : message);
            conversation.setUserId(userId);
            conversation.setModel(model);
            conversation.setMessageCount(0);
            conversation.setStatus("0");
            conversation.setCreateBy(loginName);
            conversationService.insertConversation(conversation);
            convId = conversation.getConversationId();
            // 通过SSE发送对话ID给前端
            try
            {
                emitter.send(SseEmitter.event().name("conversation").data(convId.toString()));
            }
            catch (Exception e)
            {
                log.error("发送对话ID异常", e);
            }
        }
        else
        {
            convId = conversationId;
        }

        // 保存用户消息
        AiMessage userMsg = new AiMessage();
        userMsg.setConversationId(convId);
        userMsg.setRole("user");
        userMsg.setContent(message);
        userMsg.setCreateBy(loginName);
        messageService.insertMessage(userMsg);

        // 更新对话消息计数
        AiConversation conv = conversationService.selectConversationById(convId);
        if (conv != null)
        {
            conv.setMessageCount(conv.getMessageCount() != null ? conv.getMessageCount() + 1 : 1);
            conv.setUpdateBy(loginName);
            conversationService.updateConversation(conv);
        }

        executorService.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try
            {
                String apiUrl = baseUrl + "/v1/chat/completions";
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setRequestProperty("Accept", "text/event-stream");
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(300000);

                // 构建请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", model);
                requestBody.put("stream", true);

                List<Map<String, String>> messages = new ArrayList<>();
                Map<String, String> userMessage = new HashMap<>();
                userMessage.put("role", "user");
                userMessage.put("content", message);
                messages.add(userMessage);
                requestBody.put("messages", messages);

                String jsonBody = JSON.toJSONString(requestBody);
                byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                connection.setFixedLengthStreamingMode(bodyBytes.length);

                // 发送请求
                try (OutputStream os = connection.getOutputStream())
                {
                    os.write(bodyBytes);
                    os.flush();
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 200)
                {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder errorBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        errorBuilder.append(line);
                    }
                    emitter.send(SseEmitter.event().name("error").data("AI服务请求失败：" + errorBuilder.toString()));
                    emitter.complete();
                    return;
                }

                // 读取SSE流式响应
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder fullContent = new StringBuilder();
                while ((line = reader.readLine()) != null)
                {
                    if (line.startsWith("data: "))
                    {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data))
                        {
                            emitter.send(SseEmitter.event().name("message").data("[DONE]"));
                            break;
                        }
                        try
                        {
                            JSONObject json = JSON.parseObject(data);
                            JSONArray choices = json.getJSONArray("choices");
                            if (choices != null && !choices.isEmpty())
                            {
                                JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                                if (delta != null && delta.containsKey("content"))
                                {
                                    String content = delta.getString("content");
                                    if (content != null)
                                    {
                                        fullContent.append(content);
                                        emitter.send(SseEmitter.event().name("message").data(content));
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            log.warn("解析SSE数据异常: {}", data, e);
                        }
                    }
                }

                // 流式结束后保存AI回复消息
                if (fullContent.length() > 0)
                {
                    AiMessage assistantMsg = new AiMessage();
                    assistantMsg.setConversationId(convId);
                    assistantMsg.setRole("assistant");
                    assistantMsg.setContent(fullContent.toString());
                    assistantMsg.setCreateBy(loginName);
                    messageService.insertMessage(assistantMsg);

                    // 更新对话消息计数
                    AiConversation updateConv = conversationService.selectConversationById(convId);
                    if (updateConv != null)
                    {
                        updateConv.setMessageCount(updateConv.getMessageCount() != null ? updateConv.getMessageCount() + 1 : 1);
                        updateConv.setUpdateBy(loginName);
                        conversationService.updateConversation(updateConv);
                    }
                }

                emitter.complete();
            }
            catch (Exception e)
            {
                log.error("流式AI问答请求异常", e);
                try
                {
                    emitter.send(SseEmitter.event().name("error").data("AI服务调用失败：" + e.getMessage()));
                    emitter.complete();
                }
                catch (Exception ex)
                {
                    emitter.completeWithError(ex);
                }
            }
            finally
            {
                if (reader != null)
                {
                    try { reader.close(); } catch (Exception ignored) {}
                }
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
        });

        // SSE连接超时或出错时的回调
        emitter.onTimeout(() -> log.warn("SSE连接超时"));
        emitter.onError(e -> log.error("SSE连接异常", e));

        return emitter;
    }
}
