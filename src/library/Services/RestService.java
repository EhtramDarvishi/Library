
package library.Services;


import com.sun.net.httpserver.Headers;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


public abstract class RestService {

  protected static final Logger logger = Logger.getLogger("NRS");

  public RestService() {

  }

  public RestService(RestService context) {
    this.Authorization = context.Authorization;
    this.headers = context.headers;
    this.Method = context.Method;
    this.IPAddress = context.IPAddress;
    this.URL = context.URL;
    this.Content = context.Content;
  }

//  public static HashMap<String, UserSession> htToken = new HashMap<String, UserSession>();
  private String IPAddress = "";
  private String Method = "";
  private URI URL = null;
  //private String Content = ""; //OK
  private byte[] Content;

  private HashMap<Headers, String> headers = new HashMap<>();
  private HashMap<String, String> responseHeaders = new HashMap<>();
  private byte[] binaryResponse = null;

  private String path = "";
  private String contentType = "application/json";
  private String Authorization = "";

  private String[] parameters = new String[]{};

  private int httpReturnCode = ERROR_SUCCESS;
  public static final int ERROR_SUCCESS = 200;
  public static final int ERROR_CREATED = 201;
  public static final int ERROR_BADREQUEST = 400;
  public static final int ERROR_UNAUTHORIZED = 401;
  public static final int ERROR_FORBIDDEN = 403;
  public static final int ERROR_NOCONTENT = 204;
  public static final int ERROR_SeeOther = 303;
  public static final int ERROR_NotFound = 404;  
  public static final int ERROR_MethodNotAllowed = 405;    
  public static final int ERROR_InternalServerError = 405;      

  //404 Not Found	Resource not found.
  //405 Method Not Allowed	The resource doesn't support the specified HTTP verb.
  //409 Conflict	Conflict.
  //411 Length Required	The Content-Length header was not specified.
  //412 Precondition Failed	Precondition failed.
  //429 Too Many Requests	Too many request for rate limiting.
  //500 Internal Server Error	Servers are not working as expected. The request is probably valid but needs to be requested again later.
  //503 Service Unavailable	Service Unavailable.

  public static final String mtPOST = "POST";
  public static final String mtGET = "GET";
  public static final String mtDelete = "DELETE";
  public static final String mtPUT = "PUT";
  public static final String mtPATCH = "PATCH";
  public static final String mtHEAD = "HEAD";
  public static final String mtTRACE = "TRACE";
  public static final String mtCONNECT = "CONNECT";

  public abstract String execute() throws Exception;

  protected Map<String, String> getQueryParamMap(String query) {
    //String query = getURL().getQuery();
    try {
      //query = new java.net.URI(query);
         query = java.net.URLDecoder.decode(query, StandardCharsets.UTF_8.name());
    } catch (Exception e) {
      logger.warning("Exception getQueryParamMap: " + e.getMessage());
    }
    
    if (query == null || query.isEmpty()) {
      return Collections.emptyMap();
    }
    String[] queryParams = query.split("&");
    Map<String, String> mpRes = new HashMap<String, String>();
    for (String param : queryParams) {
      if (!param.isEmpty()) {
        String[] item = param.split("=");
        if (item.length > 1) {
          mpRes.put(item[0], item[1]);
        } else {
          mpRes.put(item[0], "");
        }
      }
    }
    return mpRes;
//    return Stream.of(query.split("&"))
//            .filter(s -> !s.isEmpty())
//            .map(kv -> kv.split("=", 2))
//            .collect(Collectors.toMap(x -> x[0], x -> x[1]));

  }

  /* protected Map<String, String> getPOSTParamMap() throws Exception {
    String content = this.getContent();
    if (content == null || content.isEmpty()) {
      return null;
    }
    return null;
  }
   */
  protected String getOperationPath() {
    String[] fullPath = this.getPath().split("/");
    String ret = "";
    if (fullPath.length > 2) {
      for (int i = 3; i < fullPath.length; i++) {
        ret = fullPath[i] + "/";
      }
    }
    if (ret.endsWith("/")) {
      ret = ret.substring(0, ret.length() - 1);
    }
    return ret;
  }

  /**
   *
   * @param paramsName
   * @return String Receives Information Sent In The Body
   */
  protected String getPostParameter(String paramsName) throws Exception {
    /* this.getContent();
    this.getContentType();
    if (!this.getContentType().equals("application/json")) {
      Map<String, String> postParams = this.getQueryParamMap(this.Content);
      return postParams.get(paramsName);
    } else {
      JSONObject object = this.getContentAsJSONObject();
      return object.optString(paramsName, "");
    }*/

    //Map<String, String> postParams = this.getQueryParamMap(this.Content); //OK
    Map<String, String> postParams = this.getQueryParamMap(this.getContent_Text());
    return postParams.get(paramsName);
  }

  /**
   *
   * @param paramsName
   * @return String Receives Information sent By URL
   */
  protected String getQueryParameter(String paramsName) {
    Map<String, String> params = this.getQueryParamMap(this.getURL().getQuery());
    return params.get(paramsName);
  }

  /**
   * Get Content Convert To JSONObject
   *
   * @return JSONObject
   */
  protected JSONObject getContent_AsJSONObject() throws Exception {
    JSONObject b = new JSONObject(this.getContent_Text());
    return b;
  }

  /**
   * Get Content Convert To JSONArray
   *
   * @return JSONArray
   */
  protected JSONArray getContent_ASJSONArray() throws Exception {
    JSONArray arrayContent = new JSONArray(this.getContent_Text());
    return arrayContent;
  }

  /**
   * @return the Authorization
   */
  public String getAuthorization() {
    return Authorization;
  }

  /**
   * @param Authorization the Authorization to set
   */
  public void setAuthorization(String authorization) {
    this.Authorization = authorization;
  }

//  private RestSession restSession = null;
//
//  public RestSession getRestSession() throws Exception {
//    if (restSession == null) {
//      this.restSession = new RestSession().findByPrimaryKey(this.Authorization);
//      if (this.restSession != null) {
//        if (restSession.isExpired()) {
//          return null;
//        }
//        if (this.restSession.getLastActionDateTime_kdt().incMin(this.restSession.getIdleMinutesAllowed()).isBeforeNow())//To DO ExpiraDate For RestSession
//        {
//          return null;
//        }
//      }
//    }
//   
//
//    return this.restSession;
//  }

//  private UserSession userSession = null;
//
//  public UserSession getUserSession() throws Exception {
//    if (userSession == null) {
//
//      RestSession rs = this.getRestSession();
//      if (rs != null) {
//        this.userSession = htToken.get(rs.getRestSessionID());// RestSession.getUserSession(rs); comment by E.Darvishi //Todo: Ms. Darvishi Function Here (rs.getSessionData())
//      }
//    }
//
//    return this.userSession;
//  }/

  /**
   * @return the IPAddress
   */
  public String getIPAddress() {
    return IPAddress;
  }

  /**
   * @param IPAddress the IPAddress to set
   */
  public void setIPAddress(String IPAddress) {
    this.IPAddress = IPAddress;
  }

  /**
   * @return the Method
   */
  public String getMethod() {
    return Method;
  }

  /**
   * @param Method the Method to set
   */
  public void setMethod(String Method) {
    this.Method = Method;
  }

  /**
   * @return the URL
   */
  public URI getURL() {
    return URL;
  }

  /**
   * @param URL the URL to set
   */
  public void setURL(URI URL) throws URISyntaxException {
   // InjectionAttackWrapper wrapper = new InjectionAttackWrapper();
    String request = URL.toString();//wrapper.checkSqlInjection(URL.toString());
    URL = new URI(request);
    this.URL = URL;
  }

  /**
   * @return the Content
   */
  public String getContent_Text() {
    String ret = new String(Content, StandardCharsets.UTF_8);
    return ret;
    //return Content; //OK
  }

  /**
   * @return the Content
   */
  public byte[] getContent_Binary() {
    return Content;
  }

  /**
   * @param Content the Content to set
   */
  public void setContent(byte[] Content) {
   // InjectionAttackWrapper wrapper = new InjectionAttackWrapper();

    try {
      //String contentString = new String(Content, StandardCharsets.UTF_8);
//      System.out.println(contentString);
     // String request = wrapper.checkSqlInjection(contentString);
//      System.out.println(request);
      this.Content = Content;//contentString.getBytes();
    } catch (Exception e) {
      this.Content = Content;
    }
    
  }

  /**
   * @return the headers
   */
  public HashMap<Headers, String> getHeaders() {
    return headers;
  }

  /**
   * @param headers the headers to set
   */
  public void setHeaders(HashMap<Headers, String> headers) {
    this.headers = headers;
  }

  /**
   * @return the path return /Entities/Airport/List
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the httpReturnCode
   */
  public int getHttpReturnCode() {
    return httpReturnCode;
  }

  /**
   * @param httpReturnCode the httpReturnCode to set
   */
  protected void setHttpReturnCode(int httpReturnCode) {
    this.httpReturnCode = httpReturnCode;
  }

  /**
   * @return the contentType
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * @param contentType the contentType to set
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * @return the parameters
   */
  public String[] getParameters() {
    return parameters;
  }

  /**
   * @param parameters the parameters to set
   */
  public void setParameters(String[] parameters) {
    this.parameters = parameters;
  }

  public HashMap<String, String> getResponseHeaders() {
    return this.responseHeaders;
  }

  public String getResponseHeader(String key) {
    return this.responseHeaders.get(key);
  }

  public void addResponseHeader(String key, String value) {
    this.responseHeaders.put(key, value);
  }

  public void sendRedirect(String url) {
    this.addResponseHeader("Location", url);
    this.setHttpReturnCode(301);
    this.setContentType("");
  }

  /**
   * @return the binaryResponse
   */
  public byte[] getBinaryResponse() {
    return binaryResponse;
  }

  /**
   * @param binaryResponse the binaryResponse to set
   */
  public void setBinaryResponse(byte[] binaryResponse) {
    this.binaryResponse = binaryResponse;
  }

}

