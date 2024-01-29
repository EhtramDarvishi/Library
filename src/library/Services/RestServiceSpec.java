/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.Services;


public class RestServiceSpec {

  private String path;
  private String method;
  private String className;
  private String[] parameters; // = new String[] {};

  public RestServiceSpec(String path, String methods, String className, String[] parameters) {
    this.path = path;
    this.method = methods;
    this.className = className;
    this.parameters = parameters;
  }

  public RestServiceSpec(String path, String methods, Class klass, String[] parameters) {
    this.path = path;
    this.method = methods;
    this.className = klass.getName();
    this.parameters = parameters;
  }
  
  public RestService getService() throws Exception {
    RestService service = (RestService) Class.forName(this.className).newInstance();
    return service;
  }

  
  /**
   * @return the path
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
   * @return the method
   */
  public String getMethod() {
    return method;
  }

  /**
   * @param method the method to set
   * GET, POST, ...
   * '*' FOR ALL METHODS
   */
  public void setMethod(String method) {
    this.method = method;
  }

  /**
   * @return the Class
   */
  public String getClassName() {
    return className;
  }

  /**
   * @param Class the Class to set
   */
  public void setClassName(String className) {
    this.className = className;
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
  
}
