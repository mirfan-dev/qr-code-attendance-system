package com.irfan.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.irfan.service.QrService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static com.irfan.util.QrConstants.QR_PREFIX;

@Service
public class QrServiceImpl implements QrService {

    private static final int SIZE = 300;
    private static final String FORMAT = "PNG";

    @Override
    public String generateQrBase64(String token) throws WriterException, IOException {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token must not be blank");
        }

        String content = QR_PREFIX + token;

        BitMatrix matrix = new QRCodeWriter()
                .encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, FORMAT, out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        }
    }
}