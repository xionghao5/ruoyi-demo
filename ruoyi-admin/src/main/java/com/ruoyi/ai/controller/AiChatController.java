package com.ruoyi.ai.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.ai.service.AiChatService;

/**
 * AI智能问答控制器
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/ai/chat")
public class AiChatController extends BaseController
{
    private String prefix = "ai";

    @Autowired
    private AiChatService aiChatService;

    @RequiresPermissions("ai:chat:view")
    @GetMapping()
    public String chat()
    {
        return prefix + "/chat";
    }

    /**
     * 流式发送问题并获取AI回答（SSE）
     */
    @RequiresPermissions("ai:chat:view")
    @PostMapping(value = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter send(@RequestParam("message") String message)
    {
        if (message == null || message.trim().isEmpty())
        {
            SseEmitter emitter = new SseEmitter();
            try
            {
                emitter.send(SseEmitter.event().name("error").data("请输入问题内容"));
                emitter.complete();
            }
            catch (Exception e)
            {
                emitter.completeWithError(e);
            }
            return emitter;
        }
        return aiChatService.streamChat(message.trim());
    }
}
