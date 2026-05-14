package org.example.tennisscoreboard.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.*;

public class ServletUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int BUFFER_SIZE = 128;
    private static final int END_OF_STREAM = -1;
    private static final int OFFSET = 0;

    private static final JsonConverter JSON_CONVERTER = new JsonConverter();

    private ServletUtil() {}

    public static void sendResponse(int code, Object object, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        String json = JSON_CONVERTER.convertToJSON(object);
        response.setStatus(code);
        printWriter.println(json);
    }

    public static String getJson(HttpServletRequest request) throws IOException {

        InputStream inputStream = request.getInputStream();
        StringBuilder stringBuilder = new StringBuilder();

        if (inputStream != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                char[] charBuffer = new char[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) != END_OF_STREAM) {
                    stringBuilder.append(charBuffer, OFFSET, bytesRead);
                }
            }
        }

        return stringBuilder.toString();
    }

    public static <T> T deserialize(String json, Class<T> valueType) {
        return OBJECT_MAPPER.readValue(json, valueType);
    }
}
