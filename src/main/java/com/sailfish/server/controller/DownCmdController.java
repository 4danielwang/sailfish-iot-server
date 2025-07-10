//package com.sailfish.server.controller;
//
//import com.sailfish.server.core.dto.req.DownlinkMessageReq;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 下行指令控制器
// *
// * @author wangpeixin
// * @since 2025/7/8 15:53
// */
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class DownCmdController {
//
//    // TODO: 异步
//    @PostMapping("/onDownCmd")
//    public ResponseEntity<String> onDownCmd(DownlinkMessageReq req) {
//
//        String sessionId = req.sessionId();
//
//        // 1、查找session
//        // 2、发起下行命令
//        return ResponseEntity.ok("下行指令已发送");
//    }
//}
