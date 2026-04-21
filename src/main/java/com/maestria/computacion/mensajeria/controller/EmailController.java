package com.maestria.computacion.mensajeria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maestria.computacion.mensajeria.dto.EmailRequest;
import com.maestria.computacion.mensajeria.service.EmailService;

@RestController
@RequestMapping("/mensajeria")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/enviar-email") 
    public ResponseEntity<Boolean> enviarCorreoConAnexos(@RequestBody EmailRequest request) {
        boolean exito = emailService.enviarCorreoConAnexos(request.getCorreos(), request.getAsunto(), request.getMensaje(), request.getDocumentos());
        if (exito) {
            System.out.println("Correo electrónico enviado correctamente.");
            return ResponseEntity.ok(Boolean.TRUE);
        } else {
            System.out.println("Error al enviar el correo electrónico.");
            return ResponseEntity.internalServerError().body(Boolean.FALSE);
        }
    }
}
