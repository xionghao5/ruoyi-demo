package com.ruoyi.ai.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
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

    @RequiresPermissions("ai:chat:view")
    @PostMapping("/send")
    @ResponseBody
    public AjaxResult send(@RequestParam("message") String message)
    {
        if (message == null || message.trim().isEmpty())
        {
            return AjaxResult.error("请输入问题内容");
        }
        return aiChatService.chat(message.trim());
    }
}
