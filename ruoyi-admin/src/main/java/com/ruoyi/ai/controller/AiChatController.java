package com.ruoyi.ai.controller;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysUser;
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
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.ai.domain.AiConversation;
import com.ruoyi.ai.domain.AiMessage;
import com.ruoyi.ai.service.AiChatService;
import com.ruoyi.ai.service.IAiConversationService;
import com.ruoyi.ai.service.IAiMessageService;

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

    @Autowired
    private IAiConversationService conversationService;

    @Autowired
    private IAiMessageService messageService;

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
    public SseEmitter send(@RequestParam("message") String message,
                           @RequestParam(value = "conversationId", required = false) Long conversationId,
                           @RequestParam(value = "knowledgeId", required = false) Long knowledgeId)
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
        return aiChatService.streamChat(message.trim(), conversationId, knowledgeId);
    }

    /**
     * 查询当前用户的对话列表
     */
    @RequiresPermissions("ai:chat:view")
    @GetMapping("/list")
    @ResponseBody
    public TableDataInfo list(AiConversation conversation)
    {
        startPage();
        SysUser currentUser = ShiroUtils.getSysUser();
        conversation.setUserId(currentUser.getUserId());
        List<AiConversation> list = conversationService.selectConversationList(conversation);
        return getDataTable(list);
    }

    /**
     * 查询对话的消息历史
     */
    @RequiresPermissions("ai:chat:view")
    @GetMapping("/messages")
    @ResponseBody
    public AjaxResult messages(@RequestParam("conversationId") Long conversationId)
    {
        List<AiMessage> messages = messageService.selectMessageByConversationId(conversationId);
        return AjaxResult.success(messages);
    }

    /**
     * 删除对话
     */
    @RequiresPermissions("ai:chat:view")
    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("conversationId") Long conversationId)
    {
        return toAjax(conversationService.deleteConversationById(conversationId));
    }
}
