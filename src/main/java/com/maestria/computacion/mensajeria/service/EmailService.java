package com.maestria.computacion.mensajeria.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.maestria.computacion.mensajeria.common.Constants;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public boolean enviarCorreoConAnexos(ArrayList<String> correos, String asunto, String mensaje,
            Map<String, String> documentos) {

        try {
            Map<String, Object> templateModel = new HashMap<>();

            for (String correo : correos) {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                String saludo = obtenerSaludo();

                templateModel.put("mensaje_saludo", saludo);
                templateModel.put("cuerpoCorreoFormateado", mensaje);

                Context context = new Context();
                context.setVariables(templateModel);

                String html = templateEngine.process("emailTemplate", context);

                helper.setTo(correo);
                helper.setSubject(asunto);
                helper.setText(html, true);

                for (Map.Entry<String, String> entry : documentos.entrySet()) {
                    String nombreDocumento = entry.getKey();
                    Object valorDocumento = entry.getValue();

                    if (valorDocumento instanceof String) {
                        String base64Documento = (String) valorDocumento;
                        byte[] documentoBytes = Base64.getDecoder().decode(base64Documento);
                        ByteArrayDataSource dataSource = new ByteArrayDataSource(documentoBytes,
                                Constants.aplicacionExtension);
                        helper.addAttachment(nombreDocumento + Constants.extension, dataSource);
                    } else if (valorDocumento instanceof List) {
                        List<String> listaAnexos = (List<String>) valorDocumento;
                        for (int i = 0; i < listaAnexos.size(); i++) {
                            String base64Anexo = listaAnexos.get(i);
                            byte[] anexoBytes = Base64.getDecoder().decode(base64Anexo);
                            ByteArrayDataSource dataSource = new ByteArrayDataSource(anexoBytes,
                                    Constants.aplicacionExtension);
                            helper.addAttachment(nombreDocumento + "_" + (i + 1) + Constants.extension, dataSource);
                        }
                    }
                }

                emailSender.send(message);
            }

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String obtenerSaludo() {
        ZonedDateTime nowInColombia = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalTime currentTime = nowInColombia.toLocalTime();

        String saludo;
        if (currentTime.isBefore(LocalTime.NOON)) {
            saludo = "Buenos días";
        } else if (currentTime.isBefore(LocalTime.of(18, 0))) {
            saludo = "Buenas tardes";
        } else {
            saludo = "Buenas noches";
        }
        return saludo;
    }
}
