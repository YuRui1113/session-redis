/*
 * File: src\main\java\com\taylor\sessionredis\controllers\SessionController.java
 * Project: jpa
 * Created Date: Wednesday, December 6th 2023, 5:58:56 pm
 * Author: Rui Yu (yurui_113@hotmail.com)
 * -----
 * Last Modified: Wednesday, 6th December 2023 5:59:16 pm
 * Modified By: Rui Yu (yurui_113@hotmail.com>)
 * -----
 * Copyright (c) 2023 Rui Yu
 * -----
 * HISTORY:
 * Date                     	By       	Comments
 * -------------------------	---------	----------------------------------------------------------
 * Wednesday, December 6th 2023	Rui Yu		Initial version
 */

package com.taylor.sessionredis.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("api/v1/session")
public class SessionController {

    @GetMapping("/hello")
    public String helloWorld(HttpServletRequest request) {
        return "Hello, World!";
    }

    @GetMapping("/data/{key}")
    public String getSession(HttpSession session, @PathVariable String key) {
        Object sessionValue = session.getAttribute(key);
        System.out.println("[get]session id:" + session.getId());
        if (sessionValue != null) {
            return sessionValue.toString();
        }

        return "Session doesn't exist for key: " + key;
    }

    @PutMapping("/data/{key}")
    public ResponseEntity<String> setSession(HttpServletRequest request, @PathVariable String key,
            @RequestBody String value) {
        request.getSession().setAttribute(key, value);
        System.out.println("[set]session id:" + request.getSession().getId());

        return ResponseEntity.ok("OK");
    }
}