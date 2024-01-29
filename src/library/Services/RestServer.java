package library.Services;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RestServer implements HttpHandler {

    private static boolean shutdownFlag = false;
    public static File retToArrayByte = null;

    public RestServer() {

        try {
            System.setProperty("file.encoding", "UTF-8");
            java.lang.reflect.Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    protected static String readRequestBodyAsString(HttpExchange exchange) throws IOException {

        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        return new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
    }

    protected static byte[] readRequestBodyAsBytes(HttpExchange exchange) throws IOException {
        // logger.warning("Request Body In Function readRequestBodyAsBytes: " + exchange.getRequestBody());
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        //   logger.warning("Request Body In Function readRequestBodyAsBytes Variable buffer: " + buffer);
        byte[] targetArray = buffer.toByteArray();
        //  logger.warning("Request Body In Function readRequestBodyAsBytes Variable targetArray: " + targetArray.toString());
        return targetArray;

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println(Thread.activeCount());
//   // logger.warning("All Environment: " + System.getenv().values().toString());
//    for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
//      logger.warning("Environment : " + entry.getKey() + ":" + entry.getValue());
//    }
//    
//    logger.warning("Request Body In Function Handle: " + httpExchange.getRequestBody());
//    
        if (shutdownFlag) {
            return;
        }

        //String input = readRequestBodyAsString(httpExchange);
        byte[] input = readRequestBodyAsBytes(httpExchange);

        //System.out.println("NRS.BaseHandler.handle()");
        InputStream is = httpExchange.getRequestBody();

        Headers _headers = httpExchange.getRequestHeaders();
        //_headers.set("charset", "UTF-8");
        String authorization = (_headers.get("Authorization") == null ? "" : _headers.get("Authorization").get(0));
        String method = httpExchange.getRequestMethod();
        if (method.equals("OPTIONS")) {

            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, PATCH, DELETE, HEAD");
            httpExchange.getResponseHeaders().add("Access-Control-MAX-Age", "1000");
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "x-requested-with, Content-Type, origin, authorization, accept, client-security-token");
//      
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write("".getBytes());
            os.close();
            return;
        }

        String ipAddress = httpExchange.getRemoteAddress().getAddress().toString();
        if (ipAddress.startsWith("/")) {
            ipAddress = ipAddress.substring(1);
        }

        URI requestedUri = httpExchange.getRequestURI();
        String _path = requestedUri.getPath();
        requestedUri.getRawPath();

        String response;
        int httpReturnCode;
        try {
            String ret = checkURL(requestedUri);
            if (!ret.isEmpty()) {
                _path += ret;
            }
            System.out.println("mthod:" + method + " path:" + _path);
            RestServiceSpec serviceSpec = RestRouter.route(method, _path);

            System.out.println("method was set");
            RestService service = null;
            try {
                if (serviceSpec == null) {
                    System.out.println("Service spec was not found");
                } else {
                    service = serviceSpec.getService();
                }
            } catch (Exception e) {
                System.out.println("Exception in finding service:" + e.getMessage());
            }
            System.out.println("Rest Service:" + service);
            if (service != null) {
                service.getHeaders().put(_headers, _headers.entrySet().toString());
                service.setMethod(method);
                service.setIPAddress(ipAddress);
                service.setURL(requestedUri);
                service.setContent(input);
                service.setPath(_path);
                service.setAuthorization(authorization);
                service.setParameters(serviceSpec.getParameters());
                System.out.println("Rest Service Request:" + service.getContent_Text());
                response = service.execute();
                System.out.println("Rest Service Response:" + response);
                httpReturnCode = service.getHttpReturnCode();
                httpExchange.getResponseHeaders().add("Content-Type", service.getContentType());

                for (String key : service.getResponseHeaders().keySet()) {
                    httpExchange.getResponseHeaders().add(key, service.getResponseHeaders().get(key));
                }

            } else {
                response = "Invalid Service";
                httpReturnCode = 500;

            }

            httpExchange.sendResponseHeaders(httpReturnCode, 0);

            byte[] bytes;

            if ((service != null)) {
                if (service.getBinaryResponse() == null) {
                    //Text Response
                    bytes = response.getBytes();
                } else {
                    //Binary Response
                    bytes = service.getBinaryResponse();
                }
            } else {
                bytes = "Invalid NRS Service".getBytes(); // new byte[0];
            }

            try (BufferedOutputStream out = new BufferedOutputStream(httpExchange.getResponseBody())) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes /*response.getBytes()*/)) {
                    byte[] buffer = new byte[1024];
                    // buffer = response.getBytes("UTF-8");
                    int count;
                    while ((count = bis.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                    out.close();
                }
            }

        } catch (Exception ex) {
            if (!ex.getClass().toString().contains("StreamClosedException")) {
                response = "";
                System.out.println("Rest Service Result:" + ex.getMessage());
                httpExchange.sendResponseHeaders(500, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            }
        }

    }

    private static HttpServer server;

    public static void startServer(int portNo) throws Exception {
        //int portNo = 8000;
        //HttpServer server = HttpServer.create(new InetSocketAddress(portNo), 0);
        try {
            server = HttpServer.create(new InetSocketAddress(portNo), 0);
            server.createContext("/", new RestServer());
            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool()); // creates a default executor
            //server.setExecutor(null);
            server.start();
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }
        System.out.println("NRS HTTP REST SERVER STARTED ON PORT: " + portNo);
    }

    public static void shutdown() {
        RestServer.shutdownFlag = true;
        try {
            Thread.sleep(15000);
        } catch (Exception ex) {
        }
        server.stop(0);
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        int portNo = 8000;
        if (args.length == 1) {
            portNo = Integer.parseInt(args[0]);
        }

        System.setProperty("file.encoding", "UTF-8");
        java.lang.reflect.Field charset = Charset.class.getDeclaredField("defaultCharset");
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        startServer(portNo);
    }

    private String checkURL(URI requestedUri) throws Exception {
        String ret = requestedUri.getQuery();
        if ((ret != null) && (ret.length() <= 5) && (!ret.contains("="))) {
            ret = "Res/SL";
        } else {
            ret = "";
        }
        return ret;
    }

}
