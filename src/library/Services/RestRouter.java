package library.Services;


import java.util.HashMap;


public class RestRouter {



  public static RestServiceSpec route(String method, String path) throws Exception {
    String serviceName = getPath(path);
    if (restServices.get(serviceName + "-" + method.toUpperCase()) != null) {
      RestServiceSpec ret = (RestServiceSpec) (restServices.get(serviceName + "-" + method.toUpperCase()));
      return ret;
    } else if (restServices.get(serviceName + "-*") != null) {
      RestServiceSpec ret = (RestServiceSpec) (restServices.get(serviceName + "-*"));
      return ret;
    } else {
      System.out.println("!!!!!!!!!!! Not Found !!!!!!!!!");
      System.out.println(serviceName);
      System.out.println(method);
      return null;
    }
  }

  private static final HashMap<String, RestServiceSpec> restServices = new HashMap<>();

  public static String getPath(String path) {
    String[] arrPath = path.split("/");
    String ret = "";
    if (arrPath.length > 2) {
      ret = "/" + arrPath[1] + "/" + arrPath[2];
    } else {
      ret = path;
    }

    return ret;
  }

  private static boolean addService(RestServiceSpec rss) {
    restServices.put(rss.getPath() + "-" + rss.getMethod(), rss);

    return true;
  }

 
  static {
    restServices.put("/Entity/General", GeneralService.class);
    
  }

  public static synchronized void addSystemServices() {

  }

}

